package com.mualab.org.biz.modules.business_setup.add_edit_service.business_type;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.mualab.org.biz.modules.profile_setup.activity.AddNewBusinessTypeActivity;
import com.mualab.org.biz.modules.profile_setup.adapter.MyBusinessTypeListAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mualab.org.biz.util.Constant.REQUEST_NEW_BUSINESS_TYPE;

public class AllBusinessTypeActivity extends AppCompatActivity implements View.OnClickListener{
    private User user;
    private PreRegistrationSession bpSession;
    private long mLastClickTime = 0;

    private MyBusinessTypeListAdapter adapter;
    private List<MyBusinessType> allBusinessTypes;
    private TextView tvNoRecord;
    private RecyclerView listViewServices;
    private String bizTypeId="";
    private View topLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_business_type);
        init();
    }

    private void init() {
        if (user == null)
            user = Mualab.getInstance().getSessionManager().getUser(); // get session object
        allBusinessTypes = new ArrayList<>();
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.business_type));

        AppCompatButton btnNext = findViewById(R.id.btnNext);
        LinearLayout llAddBizType = findViewById(R.id.llAddBizType);
        LinearLayout llAddCategory = findViewById(R.id.llAddCategory);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        topLine = findViewById(R.id.topLine);
        listViewServices = findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(AllBusinessTypeActivity.this, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        // Create the adapter to convert the array to views
        adapter = new MyBusinessTypeListAdapter(AllBusinessTypeActivity.this,allBusinessTypes, new MyBusinessTypeListAdapter.BizTypeSelectListener() {
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
        ivHeaderBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddBizType.setOnClickListener(this);

        getMyBusinessType();


    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.ivHeaderBack:
                onBackPressed();
                break;

            case R.id.btnNext:
                if (allBusinessTypes.size()==0)
                    MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert("Please add business type.");
                else {
                    if (bizTypeId!=null && !bizTypeId.equals("") && !bizTypeId.isEmpty()){
                        Intent intent = new Intent(AllBusinessTypeActivity.this,NewAddedCategoryTypeActivity.class);
                        intent.putExtra("bizTypeId",bizTypeId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                    else
                        MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert("Please select any business type.");

                }

                break;

            case R.id.llAddCategory:
                if (allBusinessTypes.size()==0)
                    MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert("Please add business type.");
                else {
                    if (bizTypeId!=null && !bizTypeId.equals("") && !bizTypeId.isEmpty()){
                        Intent intent = new Intent(AllBusinessTypeActivity.this,NewAddCategoryActivity.class);
                        intent.putExtra("bizTypeId",bizTypeId);
                        startActivity(intent);
                        // finish();
                        // startActivityForResult(intent, Constant.REQUEST_NEW_CATEGORY);
                    }
                    else
                        MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert("Please select any business type.");

                }

                break;

            case R.id.llAddBizType:
                Intent intent = new Intent(AllBusinessTypeActivity.this,AddNewBusinessTypeActivity.class);
                // intent.putExtra("bizTypeId",bizTypeId);
                startActivityForResult(intent, REQUEST_NEW_BUSINESS_TYPE);

                break;
        }
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
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AllBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        getMyBusinessType();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AllBusinessTypeActivity.this, "myBusinessType", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    allBusinessTypes.clear();
                    Progress.hide(AllBusinessTypeActivity.this);
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
                    Progress.hide(AllBusinessTypeActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(AllBusinessTypeActivity.this);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setMethod(Request.Method.GET)
                .setBodyContentType("application/x-www-form-urlencoded")
        );
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private void apiForDeleteBizType(final int id,final int position){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AllBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
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

        HttpTask task = new HttpTask(new HttpTask.Builder(AllBusinessTypeActivity.this, "deleteBussinessType",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {

                                allBusinessTypes.remove(position);
                                adapter.notifyItemRemoved(position);

                                if (allBusinessTypes.size()!=0)
                                    tvNoRecord.setVisibility(View.GONE);
                                else
                                    tvNoRecord.setVisibility(View.VISIBLE);

                                MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert("Bussiness type delete successfully");

                            }else {
                                MyToast.getInstance(AllBusinessTypeActivity.this).showDasuAlert(message);
                            }
                        } catch (Exception e) {
                            Progress.hide(AllBusinessTypeActivity.this);
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
