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
import android.view.View;
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
import com.mualab.org.biz.modules.profile_setup.adapter.MyBusinessTypeListAdapter;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mualab.org.biz.util.Constant.REQUEST_NEW_BUSINESS_TYPE;

public class MyBusinessTypeActivity extends AppCompatActivity implements View.OnClickListener {
    private User user;
    private PreRegistrationSession bpSession;
    private long mLastClickTime = 0;

    private ProgressBar pbLoder;
    private MyBusinessTypeListAdapter adapter;
    private List<MyBusinessType> allBusinessTypes;
    private TextView tvNoRecord;
    private RecyclerView listViewServices;
    private String bizTypeId="";
    private View topLine;
    private List<Services> servicesList ;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_business_type);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        init();
    }

    private void init(){
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(allBusinessTypes==null) allBusinessTypes = new ArrayList<>();
        user = Mualab.getInstance().getSessionManager().getUser();
        servicesList = new ArrayList<>();

        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        tvHeaderText.setText(getString(R.string.business_type));
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
        LinearLayout llAddBizType = findViewById(R.id.llAddBizType);
        LinearLayout llAddCategory = findViewById(R.id.llAddCategory);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        topLine = findViewById(R.id.topLine);
        listViewServices = findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyBusinessTypeActivity.this, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        // Create the adapter to convert the array to views
        adapter = new MyBusinessTypeListAdapter(MyBusinessTypeActivity.this,allBusinessTypes, new MyBusinessTypeListAdapter.BizTypeSelectListener() {
            @Override
            public void onBizTypeSelect(int position, MyBusinessType businessType) {
                for (MyBusinessType type : allBusinessTypes){
                    type.isChecked = false;
                }

                adapter.notifyDataSetChanged();

                allBusinessTypes.get(position).isChecked = !allBusinessTypes.get(position).isChecked;
                adapter.notifyItemChanged(position);

                if (allBusinessTypes.get(position).isChecked)
                    bizTypeId = String.valueOf(allBusinessTypes.get(position).serviceId);
                else
                    bizTypeId = "";

            }

            @Override
            public void onBizTypeRemove(int position, MyBusinessType businessType) {
                //   apiForDeleteBizType(String.valueOf(businessType.serviceId));
                apiForDeleteBizType(businessType.serviceId,position);
            }

        });
        listViewServices.setAdapter(adapter);

        iv_back.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddBizType.setOnClickListener(this);

        TextView tvSkip = findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreRegistrationSession preSession = new PreRegistrationSession(MyBusinessTypeActivity.this);
                preSession.updateRegStep(3);
                Intent intent = new Intent(MyBusinessTypeActivity.this, AddedServiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                // finish();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllServiceData();
            }
        }).start();

        getMyBusinessType();

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
                if (allBusinessTypes.size()==0)
                    MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert("Please add business type.");
                else {
                    if (bizTypeId!=null && !bizTypeId.equals("") && !bizTypeId.isEmpty()){
                        Intent intent = new Intent(MyBusinessTypeActivity.this,MyAddedCategoryTypeActivity.class);
                        intent.putExtra("bizTypeId",bizTypeId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                    else
                        MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert("Please select any business type.");

                }

                break;

            case R.id.llAddCategory:
                if (allBusinessTypes.size()==0)
                    MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert("Please add business type.");
                else {
                    if (bizTypeId!=null && !bizTypeId.equals("") && !bizTypeId.isEmpty()){
                        Intent intent = new Intent(MyBusinessTypeActivity.this,AddNewCategoryActivity.class);
                        intent.putExtra("bizTypeId",bizTypeId);
                        startActivity(intent);
                        // finish();
                        // startActivityForResult(intent, Constant.REQUEST_NEW_CATEGORY);
                    }
                    else
                        MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert("Please select any business type.");

                }

                break;

            case R.id.llAddBizType:
                Intent intent = new Intent(MyBusinessTypeActivity.this,AddNewBusinessTypeActivity.class);
                // intent.putExtra("bizTypeId",bizTypeId);
                startActivityForResult(intent, REQUEST_NEW_BUSINESS_TYPE);

                break;
        }
    }

    private void deleteService(final int bizTypeId ) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Mualab.get().getDB().businessTypeDao().deleteAll(services.bizypeList);
                // Mualab.get().getDB().categoryDao().deleteAll(services.categoryList);

                if (servicesList.size()!=0){
                    for (Services services : servicesList){
                        if (services.serviceId == bizTypeId){
                            Mualab.get().getDB().serviceDao().delete(services);
                            break;
                        }
                    }
                }


             /*   handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (allBusinessTypes.size()!=0)
                            tvNoRecord.setVisibility(View.GONE);
                        else
                            tvNoRecord.setVisibility(View.VISIBLE);

                        MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert("Bussiness type delete successfully");
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

    private void apiForDeleteBizType(final int id,final int position){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteBizType(id,position);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(id));

        HttpTask task = new HttpTask(new HttpTask.Builder(MyBusinessTypeActivity.this, "deleteBussinessType",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {

                                deleteService(id);

                                allBusinessTypes.remove(position);
                                adapter.notifyItemRemoved(position);

                                if (allBusinessTypes.size()!=0)
                                    tvNoRecord.setVisibility(View.GONE);
                                else
                                    tvNoRecord.setVisibility(View.VISIBLE);

                                MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert("Bussiness type delete successfully");

                            }else {
                                MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert(message);
                            }
                        } catch (Exception e) {
                            Progress.hide(MyBusinessTypeActivity.this);
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


    private void apiForskip(){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForskip();
                    }
                }
            }).show();
        }

        Map<String,String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        /*params.put("dob", dateOfBirth);

        params.put("postalCode", postalCode);
        params.put("ssnLast", ssnNumber);*/

        new HttpTask(new HttpTask.Builder(MyBusinessTypeActivity.this, "skipPage", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        bpSession.updateRegStep(3);
                        //   Mualab.getInstance().getSessionManager().setBusinessProfileComplete(true);
                        Intent intent = new Intent(MyBusinessTypeActivity.this, AddedServiceActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }else MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert(message);


                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                // listener.onNext();
            }})
                //.setParam(params)
                .setAuthToken(user.authToken)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON)
                .setMethod(Request.Method.POST)
                .setProgress(true))
                .execute("skipPage");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            switch (requestCode){
                case REQUEST_NEW_BUSINESS_TYPE:
                    getMyBusinessType();
                    break;

            }
        }
    }

    private void getMyBusinessType() {
        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        getMyBusinessType();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(MyBusinessTypeActivity.this, "myBusinessType", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    pbLoder.setVisibility(View.GONE);
                    allBusinessTypes.clear();

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
                                MyBusinessType businessType = gson.fromJson(String.valueOf(object), MyBusinessType.class);
                                businessType.isChecked = false;
                                allBusinessTypes.add(businessType);
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
                        MyToast.getInstance(MyBusinessTypeActivity.this).showDasuAlert(helper.error_Messages(error));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MyBusinessTypeActivity.this,NewBusinessInfoActivity.class));
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
