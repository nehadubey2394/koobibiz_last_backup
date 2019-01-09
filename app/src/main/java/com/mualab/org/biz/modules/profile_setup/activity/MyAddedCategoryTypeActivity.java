package com.mualab.org.biz.modules.profile_setup.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.profile_setup.adapter.AddedCategoryListAdapter;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAddedCategoryTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar pbLoder;
    private AddedCategoryListAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<AddedCategory> addedCategoryList;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private String bizTypeId="",categoryId="";
    private View topLine;
    private User user;
    private List<Services> servicesList ;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_added_category);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        init();
    }

    private void init(){
        bizTypeId = getIntent().getStringExtra("bizTypeId");
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(addedCategoryList==null) addedCategoryList = new ArrayList<>();
        user = Mualab.getInstance().getSessionManager().getUser();
        servicesList = new ArrayList<>();

        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        tvHeaderText.setText("Service Category");
        ivKoobiLogo.setVisibility(View.GONE);
        tvHeaderText.setVisibility(View.VISIBLE);
        ImageView ivBasicInfo = findViewById(R.id.ivBasicInfo);
        ivBasicInfo.setImageDrawable(getResources().getDrawable(R.drawable.active_circle_img));
        ImageView ivBusinessInfo = findViewById(R.id.ivBusinessInfo);
        ivBusinessInfo.setImageDrawable(getResources().getDrawable(R.drawable.active_circle_img));
        ImageView ivServices = findViewById(R.id.ivServices);
        ivServices.setImageDrawable(getResources().getDrawable(R.drawable.inactive_green_circle_img));
        View line1 = findViewById(R.id.line1);
        line1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        View line2 = findViewById(R.id.line2);
        line2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        pbLoder = findViewById(R.id.pbLoder);
        AppCompatButton btnNext = findViewById(R.id.btnNext);
        LinearLayout llAddService = findViewById(R.id.llAddService);
        LinearLayout llAddCategory = findViewById(R.id.llAddCategory);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        topLine = findViewById(R.id.topLine);
        listViewServices = findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyAddedCategoryTypeActivity.this, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        adapter = new AddedCategoryListAdapter(MyAddedCategoryTypeActivity.this, addedCategoryList, new AddedCategoryListAdapter.CategorySelectListener() {
            @Override
            public void onBizTypeSelect(int position, AddedCategory addedCategory) {
                for (AddedCategory type : addedCategoryList){
                    type.isChecked = false;
                }

                adapter.notifyDataSetChanged();

                addedCategoryList.get(position).isChecked = !addedCategoryList.get(position).isChecked;
                adapter.notifyItemChanged(position);

                if (addedCategoryList.get(position).isChecked)
                    categoryId = String.valueOf(addedCategoryList.get(position)._id);
                else
                    categoryId = "";
            }

            @Override
            public void onCategoryRemove(int position, AddedCategory addedCategory) {
                apiForDeleteCategory(Integer.parseInt(addedCategory.subServiceId),position);
            }
        });
        listViewServices.setAdapter(adapter);

        iv_back.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddService.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllServiceData();
            }
        }).start();

        apiForGetCategory();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.btnNext:

                if (!categoryId.equals("")){
                    Intent intent = new Intent(MyAddedCategoryTypeActivity.this,AddedServiceActivity.class);
                    intent.putExtra("bizTypeId",bizTypeId);
                    intent.putExtra("categoryId",categoryId);
                    startActivity(intent);

                } else
                    MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert("Please Select any category");


                break;

            case R.id.llAddCategory:
                if (!bizTypeId.equals("")){

                    Intent intent = new Intent(MyAddedCategoryTypeActivity.this,AddNewCategoryActivity.class);
                    intent.putExtra("bizTypeId",bizTypeId);
                    startActivityForResult(intent, Constant.REQUEST_NEW_CATEGORY);

                }
                else
                    MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert("Please Select one Business Type");


                break;

            case R.id.llAddService:

                if (!categoryId.equals("")){
                    Intent intent = new Intent(MyAddedCategoryTypeActivity.this,AddNewServiceActivity.class);
                    intent.putExtra("bizTypeId",bizTypeId);
                    intent.putExtra("categoryId",categoryId);
                    startActivityForResult(intent, Constant.REQUEST_NEW_SERVICE);

                }else
                    MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert("Please select any category.");

                break;
        }
    }

    private void apiForGetCategory() {

        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyAddedCategoryTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForGetCategory();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(MyAddedCategoryTypeActivity.this,
                "mysubCategory?categoryId="+bizTypeId,
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            pbLoder.setVisibility(View.GONE);
                            addedCategoryList.clear();

                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                tvNoRecord.setVisibility(View.GONE);
                                listViewServices.setVisibility(View.VISIBLE);
                                topLine.setVisibility(View.VISIBLE);

                                JSONArray dataJsonArray = js.getJSONArray("data");
                                if (dataJsonArray != null && dataJsonArray.length() != 0) {
                                    for (int j = 0; j < dataJsonArray.length(); j++) {
                                        JSONObject object = dataJsonArray.getJSONObject(j);
                                        Gson gson = new Gson();
                                        AddedCategory addedCategory = gson.fromJson(String.valueOf(object), AddedCategory.class);
                                        addedCategory.isChecked = false;
                                        addedCategoryList.add(addedCategory);
                                    }
                                }

                            }else {
                                tvNoRecord.setVisibility(View.VISIBLE);
                                listViewServices.setVisibility(View.GONE);
                                topLine.setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        pbLoder.setVisibility(View.GONE);
                        try {
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")) {
                                Mualab.getInstance().getSessionManager().logout();
                            }else
                                MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert(helper.error_Messages(error));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                })
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setMethod(Request.Method.GET)
                .setBodyContentType("application/x-www-form-urlencoded")
        );
        pbLoder.setVisibility(View.VISIBLE);
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private void deleteService(final int catId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Mualab.get().getDB().businessTypeDao().deleteAll(services.bizypeList);
                // Mualab.get().getDB().categoryDao().deleteAll(services.categoryList);

                if (servicesList.size()!=0){
                    for (Services services : servicesList){
                        if (services.subserviceId == catId){
                            Mualab.get().getDB().serviceDao().delete(services);
                            break;
                        }
                    }

                }

           /*     handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (addedCategoryList.size()!=0)
                            tvNoRecord.setVisibility(View.GONE);
                        else
                            tvNoRecord.setVisibility(View.VISIBLE);

                        MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert("Category delete successfully");
                    }
                });*/


            }
        }).start();
    }

    private void getAllServiceData(){
        pbLoder.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                servicesList.clear();

                //   bizypeList = Mualab.get().getDB().businessTypeDao().getAll();
                // categoryList =  Mualab.get().getDB().categoryDao().getAll();
                servicesList =  Mualab.get().getDB().serviceDao().getAll();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (servicesList.size()!=0) {
                            listViewServices.setVisibility(View.VISIBLE);
                            tvNoRecord.setVisibility(View.GONE);
                        }
                        else {
                            listViewServices.setVisibility(View.GONE);
                            tvNoRecord.setVisibility(View.VISIBLE);
                        }

                        pbLoder.setVisibility(View.GONE);

                        shortList();

                    }
                });

            }
        }).start();


    }

    private void shortList() {
        Collections.sort(servicesList, new Comparator<Services>() {

            @Override
            public int compare(Services a1, Services a2) {

                if (a1.serviceId == 0 || a2.serviceId == 0)
                    return -1;
                else {
                    Long long1 = (long) a1.serviceId;
                    Long long2 = (long) a2.serviceId;
                    return long1.compareTo(long2);
                }
            }
        });

        if (servicesList.size()!=0) {
            listViewServices.setVisibility(View.VISIBLE);
            tvNoRecord.setVisibility(View.GONE);
        }
        else {
            listViewServices.setVisibility(View.GONE);
            tvNoRecord.setVisibility(View.VISIBLE);
        }

    }

    private void apiForDeleteCategory(final int id,final int position){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyAddedCategoryTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteCategory(id,position);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(id));

        HttpTask task = new HttpTask(new HttpTask.Builder(MyAddedCategoryTypeActivity.this, "deleteCategory",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {

                                deleteService(id);

                                addedCategoryList.remove(position);
                                adapter.notifyItemRemoved(position);

                                if (addedCategoryList.size()!=0)
                                    tvNoRecord.setVisibility(View.GONE);
                                else
                                    tvNoRecord.setVisibility(View.VISIBLE);

                                MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert(message);

                            }else {
                                MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert(message);
                            }
                        } catch (Exception e) {
                            Progress.hide(MyAddedCategoryTypeActivity.this);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        try{
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")){
                                Mualab.getInstance().getSessionManager().logout();
                                //      MyToast.getInstance(BookingActivity.this).showSmallCustomToast(helper.error_Messages(error));
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case Constant.REQUEST_NEW_CATEGORY:
                       /* if (addedCategoryList!=null && addedCategoryList.size()!=0){
                            for (AddedCategory type : addedCategoryList){
                                type.isChecked = false;
                            }

                            adapter.notifyDataSetChanged();
                            categoryId = "";
                            addedCategoryList.clear();
                        }*/
                        apiForGetCategory();
                        break;

                    case Constant.REQUEST_NEW_SERVICE:
                      /*  BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                        BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                                (getActivity()) != null ?
                                        (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                                activity.mPagerAdapter : null;

                        if (adapter != null && viewPager!=null) {

                            listener.onChangeByTag("company_services");

                            AddedServicesFragment fragment = (AddedServicesFragment)
                                    adapter.getRegisteredFragment(viewPager.getCurrentItem());

                            fragment.getCategoryAndBizTypeId(bizTypeId,categoryId);
                        }
                        break;*/
                }
            }
        }
    }

    private boolean checkNotempty(EditText editText,String msg) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            MyToast.getInstance(MyAddedCategoryTypeActivity.this).showDasuAlert(msg);
            editText.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyAddedCategoryTypeActivity.this,MyBusinessTypeActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(3);
        super.onPause();
    }

    @Override
    protected void onStop() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(3);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(3);
        super.onDestroy();
    }

}
