package com.mualab.org.biz.modules.profile.activity;

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
import com.google.gson.JsonArray;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.profile.adapter.AddedServicesAdapter;
import com.mualab.org.biz.modules.profile.db_modle.Services;
import com.mualab.org.biz.modules.profile.fragment.NewBusinessCategoryFragments.AddedServicesFragment;
import com.mualab.org.biz.modules.profile.modle.AddedCategory;
import com.mualab.org.biz.modules.profile.modle.FinalServiceForUpdateServer;
import com.mualab.org.biz.modules.profile.modle.MyBusinessType;
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

public class AddedServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar pbLoder;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private String bizTypeId="",categoryId="";
    private View topLine;
    private List<Services> servicesList ;
    private Handler handler = new Handler(Looper.getMainLooper());
    private AddedServicesAdapter servicesListAdapter;
    private List<Services>tempServicesList;
    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_added_service);
        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object
        servicesList = new ArrayList<>();
        tempServicesList = new ArrayList<>();
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

        servicesListAdapter = new AddedServicesAdapter(AddedServiceActivity.this, tempServicesList,
                new AddedServicesAdapter.onClickListener() {
                    @Override
                    public void onEditClick(int pos) {
                        Intent intent = new Intent(AddedServiceActivity.this,EditServiceActivity.class);
                        intent.putExtra("serviceItem",tempServicesList.get(pos));
                        startActivityForResult(intent, Constant.EDIT_SERVICE);
                    }

                    @Override
                    public void onDelClick(int pos) {
                        deleteService(tempServicesList.get(pos));
                        tempServicesList.remove(pos);
                        servicesListAdapter.notifyItemRemoved(pos);
                    }
                });

        listViewServices = findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(AddedServiceActivity.this, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);
//        rycRecipeList.setHasFixedSize(true);
        listViewServices.setAdapter(servicesListAdapter);

        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddService.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        TextView tvSkip = findViewById(R.id.tvSkip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreRegistrationSession preSession = new PreRegistrationSession(AddedServiceActivity.this);
                preSession.updateRegStep(3);
                //apiForskip();
                Intent intent = new Intent(AddedServiceActivity.this, AddBankAccountActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        getAllServiceData();
    }

    private void deleteService(final Services services) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Mualab.get().getDB().businessTypeDao().deleteAll(services.bizypeList);
                // Mualab.get().getDB().categoryDao().deleteAll(services.categoryList);
                Mualab.get().getDB().serviceDao().delete(services);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tempServicesList.size()!=0)
                            tvNoRecord.setVisibility(View.GONE);
                        else
                            tvNoRecord.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
    }

    private void apiForskip(){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddedServiceActivity.this, new NoConnectionDialog.Listner() {
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

        new HttpTask(new HttpTask.Builder(AddedServiceActivity.this, "skipPage", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        PreRegistrationSession preSession = new PreRegistrationSession(AddedServiceActivity.this);
                        preSession.updateRegStep(3);
                        //   Mualab.getInstance().getSessionManager().setBusinessProfileComplete(true);
                        Intent intent = new Intent(AddedServiceActivity.this, AddBankAccountActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //  finish();
                    }else MyToast.getInstance(AddedServiceActivity.this).showDasuAlert(message);


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
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.btnNext:
                List<FinalServiceForUpdateServer>finalServiceForUpdateServers = new ArrayList<>();
                for (int i=0; i<tempServicesList.size();i++){
                    Services services = tempServicesList.get(i);
                    FinalServiceForUpdateServer forUpdateServer = new FinalServiceForUpdateServer();
                    forUpdateServer.serviceId = String.valueOf(services.serviceId);
                    forUpdateServer.subserviceId = String.valueOf(services.subserviceId);
                    forUpdateServer.description = services.description;
                    forUpdateServer.completionTime = services.completionTime;
                    forUpdateServer.title = services.serviceName;
                    forUpdateServer.outCallPrice = String.valueOf(services.outCallPrice);
                    forUpdateServer.inCallPrice = String.valueOf(services.inCallPrice);
                    finalServiceForUpdateServers.add(forUpdateServer);
                }

                Gson gson = new Gson();
                JsonArray jsonArray = gson.toJsonTree(finalServiceForUpdateServers).getAsJsonArray();
                if (!jsonArray.isJsonNull() && jsonArray.size()!=0)
                    apiAddService(jsonArray);
                else
                    MyToast.getInstance(AddedServiceActivity.this).showDasuAlert("Please add service");


                break;
            case R.id.llAddCategory:
                startActivity(new Intent(AddedServiceActivity.this,MyBusinessTypeActivity.class));
                //finish();

                break;

            case R.id.iv_back:
                finish();
                break;
            case R.id.llAddService:
                Intent intent = new Intent(AddedServiceActivity.this,AddNewServiceActivity.class);
                intent.putExtra("bizTypeId",bizTypeId);
                intent.putExtra("categoryId",categoryId);
                startActivityForResult(intent, Constant.REQUEST_NEW_SERVICE);
                break;
        }
    }

    private void getAllServiceData(){
        pbLoder.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                tempServicesList.clear();

                //   bizypeList = Mualab.get().getDB().businessTypeDao().getAll();
                // categoryList =  Mualab.get().getDB().categoryDao().getAll();
                servicesList =  Mualab.get().getDB().serviceDao().getAll();

                if (servicesList.size()!=0){
                    tempServicesList.addAll(servicesList);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tempServicesList.size()!=0) {
                            listViewServices.setVisibility(View.VISIBLE);
                            tvNoRecord.setVisibility(View.GONE);
                        }
                        else {
                            listViewServices.setVisibility(View.GONE);
                            tvNoRecord.setVisibility(View.VISIBLE);
                        }

                        pbLoder.setVisibility(View.GONE);

                        shortList();

                        if (tempServicesList.size()==0)
                            apiForGetService();

                    }
                });

            }
        }).start();


    }

    private void apiForGetService(){
        Progress.hide(AddedServiceActivity.this);
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddedServiceActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetService();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(AddedServiceActivity.this, "artistService",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                tvNoRecord.setVisibility(View.GONE);
                                listViewServices.setVisibility(View.VISIBLE);
                                topLine.setVisibility(View.VISIBLE);

                                JSONArray allServiceArray = js.getJSONArray("artistServices");

                                if (allServiceArray!=null) {
                                    for (int j=0; j<allServiceArray.length(); j++){
                                        JSONObject object = allServiceArray.getJSONObject(j);
                                        MyBusinessType businessType = new MyBusinessType();
                                        businessType.serviceId = Integer.parseInt(object.getString("serviceId"));
                                        businessType.serviceName = object.getString("serviceName");

                                        JSONArray subServiesArray = object.getJSONArray("subServies");
                                        if (subServiesArray!=null) {
                                            for (int k=0; k<subServiesArray.length(); k++){
                                                JSONObject jObj = subServiesArray.getJSONObject(k);
                                                AddedCategory subServices = new AddedCategory();
                                                subServices._id = Integer.parseInt(jObj.getString("_id"));
                                                subServices.serviceId = Integer.parseInt(jObj.getString("serviceId"));
                                                subServices.subServiceId = jObj.getString("subServiceId");
                                                subServices.subServiceName = jObj.getString("subServiceName");
                                                // services.categoryList.add(subServices);

                                                JSONArray artistservices = jObj.getJSONArray("artistservices");
                                                for (int m=0; m<artistservices.length(); m++){
                                                    Services services = new Services();
                                                    JSONObject jsonObject3 = artistservices.getJSONObject(m);
                                                    services.serviceId = businessType.serviceId;
                                                    services.bizTypeName = businessType.serviceName;
                                                    services.subserviceId = Integer.parseInt(subServices.subServiceId);
                                                    services.subserviceName = subServices.subServiceName;
                                                    services.id = Integer.parseInt(jsonObject3.getString("_id"));
                                                    services.serviceName = jsonObject3.getString("title");
                                                    services.completionTime = jsonObject3.getString("completionTime");
                                                    services.description = jsonObject3.getString("description");
                                                    services.bookingType = jsonObject3.getString("bookingType");

                                                    services.inCallPrice = Double.parseDouble(jsonObject3.getString("inCallPrice"));
                                                    services.outCallPrice = Double.parseDouble(jsonObject3.getString("outCallPrice"));
                                                    tempServicesList.add(services);
                                                }
                                            }
                                        }
                                        // servicesList.add(services);
                                    }
                                }else {
                                    tvNoRecord.setVisibility(View.VISIBLE);
                                    listViewServices.setVisibility(View.GONE);
                                    topLine.setVisibility(View.GONE);
                                }

                            }else {
                                tvNoRecord.setVisibility(View.VISIBLE);
                                listViewServices.setVisibility(View.GONE);
                                topLine.setVisibility(View.GONE);
                            }
                            shortList();

                        } catch (Exception e) {
                            Progress.hide(AddedServiceActivity.this);
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
                                MyToast.getInstance(AddedServiceActivity.this).showDasuAlert(helper.error_Messages(error));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }})
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        task.execute(this.getClass().getName());
    }

    private void shortList() {
        Collections.sort(tempServicesList, new Comparator<Services>() {

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
        servicesListAdapter.notifyDataSetChanged();

        if (tempServicesList.size()!=0) {
            listViewServices.setVisibility(View.VISIBLE);
            tvNoRecord.setVisibility(View.GONE);
        }
        else {
            listViewServices.setVisibility(View.GONE);
            tvNoRecord.setVisibility(View.VISIBLE);
        }

        //if (tempServicesList.size()==0)
        // apiForGetService();
    }

    /*update service data local db_modle to server db_modle*/

    private void apiAddService(final JsonArray jsonArray) {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddedServiceActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddService(jsonArray);
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("artistService", String.valueOf(jsonArray));

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddedServiceActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddService(jsonArray);
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AddedServiceActivity.this,
                "addArtistService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        Progress.hide(AddedServiceActivity.this);
                        startActivity(new Intent(AddedServiceActivity.this,AddBankAccountActivity.class));

                    }else {
                        MyToast.getInstance(AddedServiceActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


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
                        MyToast.getInstance(AddedServiceActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(body, HttpTask.ContentType.APPLICATION_JSON));

        task.execute(AddedServicesFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_NEW_SERVICE:
                    getAllServiceData();
                    break;

                case Constant.EDIT_SERVICE:
                    getAllServiceData();
                    break;
            }
        }

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
