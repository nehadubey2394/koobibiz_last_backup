package com.mualab.org.biz.modules.business_setup.add_edit_service;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.profile_setup.activity.AddServiceFieldsActivity;
import com.mualab.org.biz.modules.profile_setup.adapter.CategorySppinnerAdapter;
import com.mualab.org.biz.modules.profile_setup.adapter.CustomSppinnerAdapter;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;
import com.mualab.org.biz.session.PreRegistrationSession;
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

public class AddMoreServiceActivity extends AppCompatActivity implements View.OnClickListener {
    private long mLastClickTime = 0;
    private  User user;
    private String sServoiceName="",sInCallPrice="",sOutCallPrice="",sServiceDesc="",
            sBookingType="",complieTime="";
    private List<MyBusinessType> allBusinessTypes,tempBizypeList;
    private List<AddedCategory> addedCategoryList,tempCategoryList;
    private String bizTypeId = "",categoryId;
    private TextView tvBizType,tvCategory,textv2,textv1,tvCompletionTime;
    private CustomSppinnerAdapter adapterBizType;
    private CategorySppinnerAdapter adapterCategory;
    private Spinner spCategory;
    private List<Services>tempServicesList;
    private String commingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_service);
        init();
    }

    private void init(){
        user = Mualab.getInstance().getSessionManager().getUser();
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.add_services));

        if (getIntent().hasExtra("bundle")) {
            Bundle args = getIntent().getBundleExtra("bundle");
            tempServicesList = (ArrayList<Services>) args.getSerializable("servicesList");
        }else
            tempServicesList = new ArrayList<>();

        commingFrom = getIntent().getStringExtra("commingFrom");

        allBusinessTypes = new ArrayList<>();
        tempServicesList = new ArrayList<>();
        addedCategoryList = new ArrayList<>();
        tempBizypeList = new ArrayList<>();
        tempCategoryList = new ArrayList<>();

        adapterBizType = new CustomSppinnerAdapter(AddMoreServiceActivity.this,
                allBusinessTypes);

        adapterCategory = new CategorySppinnerAdapter(AddMoreServiceActivity.this,
                addedCategoryList);

        apiGetMyBusinessType();

        final Spinner spBizType = findViewById(R.id.spBizType);
        spCategory = findViewById(R.id.spCategory);
        View lineBookingType = findViewById(R.id.lineBookingType);


        tvBizType = findViewById(R.id.tvBizType);
        tvCategory = findViewById(R.id.tvCategory);
        textv1 = findViewById(R.id.textv1);
        textv2 = findViewById(R.id.textv2);
        tvCompletionTime = findViewById(R.id.tvCompletionTime);


        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        RelativeLayout rlBusinessType = findViewById(R.id.rlBusinessType);
        RelativeLayout rlCategory = findViewById(R.id.rlCategory);
        RelativeLayout rlServiceName = findViewById(R.id.rlServiceName);
        RelativeLayout rlPrice = findViewById(R.id.rlPrice);
        RelativeLayout rlBookingType = findViewById(R.id.rlBookingType);
        RelativeLayout rlComplitionTime = findViewById(R.id.rlComplitionTime);
        RelativeLayout rlServiceDesc = findViewById(R.id.rlServiceDesc);

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        BusinessProfile bsp  = pSession.getBusinessProfile();
        int serviceType  = pSession.getServiceType();

        if ( serviceType==3){
            rlBookingType.setVisibility(View.VISIBLE);
            lineBookingType.setVisibility(View.VISIBLE);
            sBookingType = "Both";
        }else if (serviceType==2) {
            sBookingType = "Outcall";
            rlBookingType.setVisibility(View.GONE);
            lineBookingType.setVisibility(View.GONE);
        }
        else {
            sBookingType = "Incall";
            rlBookingType.setVisibility(View.GONE);
            lineBookingType.setVisibility(View.GONE);
        }

        spBizType.setAdapter(adapterBizType);
        spCategory.setAdapter(adapterCategory);
        //   spCategory.setPrompt("");

        spBizType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempCategoryList.clear();
                tempBizypeList.clear();
                TextView textView =  view.findViewById(R.id.tvSpItem);
                textView.setText("");

                MyBusinessType businessType = allBusinessTypes.get(i);
                tempBizypeList.add(businessType);
                bizTypeId = String.valueOf(businessType.serviceId);
                textv1.setText(getString(R.string.business_type));
                // textView.setText(serviceName);
                tvBizType.setVisibility(View.VISIBLE);
                tvBizType.setText(businessType.serviceName);

                textv2.setText(getString(R.string.category));
                tvCategory.setVisibility(View.GONE);
                tvCategory.setText("");

                apiForGetCategory();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // textv1.setText(getString(R.string.business_type));
                // tvBizType.setVisibility(View.GONE);
            }
        });


        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tempCategoryList.clear();
                TextView textView =  view.findViewById(R.id.tvSpItem);
                textView.setText("");
                AddedCategory category = addedCategoryList.get(i);
                tempCategoryList.add(category);
                // spBizType.setPrompt("");
                categoryId = category.subServiceId;
                textv2.setText(getString(R.string.category));
                // textView.setText(serviceName);
                tvCategory.setVisibility(View.VISIBLE);
                tvCategory.setText(category.subServiceName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // textv2.setText(getString(R.string.business_type));
                //tvCategory.setVisibility(View.GONE);
            }
        });

        rlBusinessType.setOnClickListener(this);
        rlCategory.setOnClickListener(this);
        rlServiceName.setOnClickListener(this);
        rlBookingType.setOnClickListener(this);
        rlPrice.setOnClickListener(this);
        rlServiceDesc.setOnClickListener(this);
        rlComplitionTime.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        ivHeaderBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        boolean isAlreadyAdded = false;

        switch (view.getId()){
            case R.id.ivHeaderBack:
                onBackPressed();
                break;

            case R.id.btnContinue:
                // MyBusinessType bizType = new MyBusinessType(Integer.parseInt(businessType.serviceId), businessType.serviceName);
                if (tempServicesList.size()==0){
                    vailidateService();
                }else {
                    if (!sServoiceName.equals("")){
                        for (int i=0;i<tempServicesList.size();i++){
                            Services services = tempServicesList.get(i);
                            if (services.serviceName.equalsIgnoreCase(sServoiceName)){
                                isAlreadyAdded = true;
                                break;
                            }
                        }

                        if (isAlreadyAdded) {
                            MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Service already exist");
                        }else
                            vailidateService();
                    }else {
                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please enter service name");
                    }

                }


                break;

            case R.id.rlCategory:
                break;

            case R.id.rlServiceName:
                Intent intent1 = new Intent(AddMoreServiceActivity.this,AddServiceFieldsActivity.class);
                intent1.putExtra("serviceName",sServoiceName);
                intent1.putExtra("keyField","serviceName");
                intent1.putExtra("commingFrom", "AddMoreServiceActivity");

                startActivityForResult(intent1,10);
                break;

            case R.id.rlPrice:
                Intent intent2 = new Intent(AddMoreServiceActivity.this,AddServiceFieldsActivity.class);
                intent2.putExtra("inCallPrice",sInCallPrice);
                intent2.putExtra("outCallPrice",sOutCallPrice);
                intent2.putExtra("bookingType",sBookingType);
                intent2.putExtra("keyField","price");
                intent2.putExtra("commingFrom", "AddMoreServiceActivity");
                startActivityForResult(intent2,20);
                break;

            case R.id.rlBookingType:
                Intent intent3 = new Intent(AddMoreServiceActivity.this,AddServiceFieldsActivity.class);
                intent3.putExtra("bookingType",sBookingType);
                intent3.putExtra("keyField","bookingType");
                intent3.putExtra("commingFrom", "AddMoreServiceActivity");
                startActivityForResult(intent3,30);
                break;

            case R.id.rlServiceDesc:
                Intent intent4 = new Intent(AddMoreServiceActivity.this,AddServiceFieldsActivity.class);
                intent4.putExtra("serviceDesc",sServiceDesc);
                intent4.putExtra("keyField","serviceDesc");
                intent4.putExtra("commingFrom", "AddMoreServiceActivity");
                startActivityForResult(intent4,40);
                break;

            case R.id.rlComplitionTime:
                Intent intent5 = new Intent(AddMoreServiceActivity.this,AddServiceFieldsActivity.class);
                intent5.putExtra("complieTime",complieTime);
                intent5.putExtra("keyField","complieTime");
                intent5.putExtra("commingFrom", "AddMoreServiceActivity");
                startActivityForResult(intent5,50);
                // showPicker(getString(R.string.time_for_completion));
                break;
        }

    }

    private void vailidateService(){
        if (tempBizypeList.size()!=0) {
            if (tempCategoryList.size() != 0) {
                Services services = new Services();
                services.businessType = allBusinessTypes;
                services.serviceId = tempBizypeList.get(0).serviceId;
                services.bizTypeName = tempBizypeList.get(0).serviceName;
                services.subserviceName = tempCategoryList.get(0).subServiceName;
                services.subserviceId = Integer.parseInt(tempCategoryList.get(0).subServiceId);
                services.bookingType = sBookingType;
                services.serviceName = sServoiceName;
                services.description = sServiceDesc;
                services.completionTime = complieTime;

                if (!sInCallPrice.isEmpty())
                    services.inCallPrice = Double.parseDouble(sInCallPrice);
                else
                    services.inCallPrice = 0;

                if (!sOutCallPrice.isEmpty())
                    services.outCallPrice = Double.parseDouble(sOutCallPrice);
                else
                    services.outCallPrice = 0;

                if (!sServoiceName.equals("")){

                    if (!sServiceDesc.equals("")){

                        if (!complieTime.equals("")){

                            switch (sBookingType) {
                                case "Incall":
                                    if (!sInCallPrice.isEmpty()) {
                                       /* List<Services> businessTypeList = new ArrayList<>();
                                        businessTypeList.add(services);
                                        insertServices(businessTypeList);*/
                                        apiAddService(services);
                                    } else {
                                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please enter service price");
                                    }
                                    break;
                                case "Outcall":
                                    if (!sOutCallPrice.isEmpty()) {
                                       /* List<Services> businessTypeList = new ArrayList<>();
                                        businessTypeList.add(services);
                                        insertServices(businessTypeList);*/
                                        apiAddService(services);
                                    } else {
                                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please enter service price");
                                    }

                                    break;
                                default:
                                    if (!sInCallPrice.isEmpty() && !sOutCallPrice.isEmpty()) {
                                      /*  List<Services> businessTypeList = new ArrayList<>();
                                        businessTypeList.add(services);
                                        insertServices(businessTypeList);*/
                                        apiAddService(services);
                                    } else {
                                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please enter service price");
                                    }
                                    break;
                            }

                                  /*  List<company_services> businessTypeList = new ArrayList<>();
                                    businessTypeList.add(services);

                                    insertServices(businessTypeList);*/

                        }else {
                            MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please select compilation time for" +
                                    " service");
                        }

                    }else {
                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please enter service description");
                    }

                }else {
                    MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please enter service name");
                }

            } else {
                MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please select category");
            }
        } else {
            MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("Please select business type");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null && resultCode != 0){
            switch (requestCode){
                case 10:
                    sServoiceName =  data.getStringExtra("serviceName");
                    break;
                case 20:
                    if (data.hasExtra("outCallPrice"))
                        sOutCallPrice =  data.getStringExtra("outCallPrice");
                    if (data.hasExtra("inCallPrice"))
                        sInCallPrice =  data.getStringExtra("inCallPrice");

                    break;
                case 30:
                    sBookingType =  data.getStringExtra("bookingType");
                    sOutCallPrice =  "";
                    sInCallPrice =  "";
                    break;
                case 40:
                    sServiceDesc =  data.getStringExtra("serviceDesc");
                    break;

                case 50:
                    complieTime =  data.getStringExtra("complieTime");
                    break;
            }
        }

    }

    private void apiGetMyBusinessType() {
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddMoreServiceActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiGetMyBusinessType();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AddMoreServiceActivity.this, "myBusinessType", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    allBusinessTypes.clear();

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {

                        JSONArray dataJsonArray = js.getJSONArray("data");
                        if (dataJsonArray != null && dataJsonArray.length() != 0) {
                            for (int j = 0; j < dataJsonArray.length(); j++) {
                                JSONObject object = dataJsonArray.getJSONObject(j);
                                Gson gson = new Gson();
                                MyBusinessType businessType = gson.fromJson(String.valueOf(object), MyBusinessType.class);
                                businessType.isChecked = false;
                                if (!businessType.status.equals("0"))
                                    allBusinessTypes.add(businessType);

                            }
                        }

                    }else {
                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert(message);
                    }
                    adapterBizType.notifyDataSetChanged();

                }catch (Exception e) {
                    Progress.hide(AddMoreServiceActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(AddMoreServiceActivity.this);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert(helper.error_Messages(error));
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
        task.execute(this.getClass().getName());
    }

    private void apiForGetCategory() {
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddMoreServiceActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForGetCategory();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AddMoreServiceActivity.this,
                "mysubCategory?categoryId="+bizTypeId,
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            addedCategoryList.clear();

                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                JSONArray dataJsonArray = js.getJSONArray("data");
                                if (dataJsonArray != null && dataJsonArray.length() != 0) {
                                    for (int j = 0; j < dataJsonArray.length(); j++) {
                                        JSONObject object = dataJsonArray.getJSONObject(j);
                                        Gson gson = new Gson();
                                        AddedCategory addedCategory = gson.fromJson(String.valueOf(object), AddedCategory.class);
                                        addedCategory.isChecked = false;
                                        if (!addedCategory.status.equals("0"))
                                            addedCategoryList.add(addedCategory);
                                    }
                                }

                            }/*else {
                                MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert("No category found");
                            }*/
                            if (addedCategoryList.size()==0) {
                                tvCategory.setVisibility(View.VISIBLE);
                                tvCategory.setText(" No category available");
                            }

                            spCategory.setAdapter(adapterCategory);
                            //  adapterCategory.notifyDataSetChanged();

                        }catch (Exception e) {
                            Progress.hide(AddMoreServiceActivity.this);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        Progress.hide(AddMoreServiceActivity.this);
                        try {
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")) {
                                Mualab.getInstance().getSessionManager().logout();
                            }else
                                MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert(helper.error_Messages(error));
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
        task.execute(this.getClass().getName());
    }

    private void apiAddService(final Services services) {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddMoreServiceActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddService(services);
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("serviceId", String.valueOf(services.serviceId));
        body.put("subserviceId", String.valueOf(services.subserviceId));
        body.put("title", services.serviceName);
        body.put("description", services.description);
        body.put("inCallPrice", String.valueOf(services.inCallPrice));
        body.put("outCallPrice", String.valueOf(services.outCallPrice));
        body.put("completionTime", services.completionTime);
        body.put("id", "");


        HttpTask task = new HttpTask(new HttpTask.Builder(AddMoreServiceActivity.this,
                "addService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    Progress.hide(AddMoreServiceActivity.this);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        if (commingFrom.equals("MyServicesActivity")) {
                            setResult(RESULT_OK);
                            finish();
                        }else {
                            Intent intent = new Intent(AddMoreServiceActivity.this,MyServicesActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }

                    }else {
                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


                }catch (Exception e) {
                    Progress.hide(AddMoreServiceActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Progress.hide(AddMoreServiceActivity.this);
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(AddMoreServiceActivity.this).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setParam(body));

        task.execute(AddMoreServiceActivity.class.getName());
    }


}
