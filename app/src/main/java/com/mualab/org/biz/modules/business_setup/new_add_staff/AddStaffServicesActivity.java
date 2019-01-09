package com.mualab.org.biz.modules.business_setup.new_add_staff;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AddedStaffServices;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.modules.business_setup.new_add_staff.adapter.NewServicesAdapter;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;
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

public class AddStaffServicesActivity extends AppCompatActivity implements View.OnClickListener {

    private  String TAG = AddStaffServicesActivity.class.getName();

    private RecyclerView rvServices;
    private   long mLastClickTime = 0;
    private ProgressBar pbLoder;
    private TextView tvNoRecord;
    private NewServicesAdapter servicesListAdapter;
    private List<Services>tempServicesList;
    public static HashMap<String, Services> localServiceMap = new HashMap<>();
    private StaffDetail staffDetail;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_service);
        initView();
    }

    private void initView(){

        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffDetail = (StaffDetail) args.getSerializable("staffDetail");
        }
        user = Mualab.getInstance().getSessionManager().getUser();
        // if(mContext instanceof AddNewStaffActivity) {
        //   ((AddNewStaffActivity) mContext).setTitle();
        pbLoder = findViewById(R.id.pbLoder);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        tvHeaderTitle.setText(getString(R.string.text_services));

        tempServicesList = new ArrayList<>();

        servicesListAdapter = new NewServicesAdapter(AddStaffServicesActivity.this, tempServicesList,
                new NewServicesAdapter.onClickListener() {
                    @Override
                    public void onEditClick(int pos) {
                       /* Intent intent = new Intent(AddStaffServicesActivity.this,EditServiceActivity.class);
                        intent.putExtra("serviceItem",tempServicesList.get(pos));
                        startActivityForResult(intent, Constant.EDIT_SERVICE);*/
                        Services item = tempServicesList.get(pos);
                        Intent intent = new Intent(AddStaffServicesActivity.this,AddStaffServiceDetailActivity.class);
                        intent.putExtra("serviceItem",item);
                        startActivityForResult(intent, 20);
                    }

                    @Override
                    public void onDelClick(int pos) {
                       /* deleteService(tempServicesList.get(pos));
                        tempServicesList.remove(pos);
                        servicesListAdapter.notifyItemRemoved(pos);*/

                        Services services = tempServicesList.get(pos);

                        if ( localServiceMap.size()>1 || staffDetail.staffServices.size()>1){

                            if (services.edtInCallPrice!=0 || services.edtOutCallPrice!=0
                                    || !services.edtCompletionTime.isEmpty()) {
                                localServiceMap.remove(String.valueOf(services.id));
                                services.edtBookingType = "";
                                services.edtCompletionTime = "";
                                services.edtOutCallPrice = 0.0;
                                services.edtInCallPrice = 0.0;
                                services.isSelected = false;
                                services.isInCallEdited = "";
                                services.isOutCallEdited = "";
                                servicesListAdapter.notifyItemChanged(pos);
                            }
                            else
                                apiForDeleteBookedService(services);

                        }else {
                            MyToast.getInstance(AddStaffServicesActivity.this).showDasuAlert("You have to keep at least one staff service");
                        }
                    }

                    @Override
                    public void onClick(int pos) {
                        Services item = tempServicesList.get(pos);
                        Intent intent = new Intent(AddStaffServicesActivity.this,AddStaffServiceDetailActivity.class);
                        intent.putExtra("serviceItem",item);
                        startActivityForResult(intent, 20);
                    }
                });

        rvServices = findViewById(R.id.rvServices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddStaffServicesActivity.this);
        rvServices.setLayoutManager(layoutManager);
        rvServices.setAdapter(servicesListAdapter);

        ivHeaderBack.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

        apiForGetAllServices();
    }

    private void apiForGetAllServices(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddStaffServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetAllServices();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(AddStaffServicesActivity.this, "artistService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        tvNoRecord.setVisibility(View.GONE);
                        rvServices.setVisibility(View.VISIBLE);

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
                                            services.bookingType = jsonObject3.getString("bookingType");
                                            services.completionTime = jsonObject3.getString("completionTime");
                                            services.description = jsonObject3.getString("description");
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
                            rvServices.setVisibility(View.GONE);
                        }

                    }else {
                        tvNoRecord.setVisibility(View.VISIBLE);
                        rvServices.setVisibility(View.GONE);
                    }

                    shortList();

                } catch (Exception e) {
                    Progress.hide(AddStaffServicesActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(true)
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

        if (tempServicesList.size()!=0){
            if (localServiceMap.size()!=0){
                for(Map.Entry<String, Services> entry : localServiceMap.entrySet()){
                    Services  selectedServices = entry.getValue();
                    for (Services artistServices : tempServicesList) {
                        if (artistServices.id == selectedServices.id) {
                            artistServices.isSelected = true;
                            artistServices.inCallPrice = selectedServices.inCallPrice;
                            artistServices.outCallPrice = selectedServices.outCallPrice;
                            artistServices.completionTime = selectedServices.completionTime;

                            artistServices.edtBookingType = selectedServices.edtBookingType;
                            artistServices.edtInCallPrice = selectedServices.edtInCallPrice;
                            artistServices.edtOutCallPrice = selectedServices.edtOutCallPrice;
                            artistServices.edtCompletionTime = selectedServices.edtCompletionTime;
                            localServiceMap.put(String.valueOf(artistServices.id),artistServices);
                        }
                    }
                }
            }
            //     else {
            if (staffDetail.staffServices.size()!=0){
                for (AddedStaffServices staffServices : staffDetail.staffServices){
                    for (Services artistServices : tempServicesList) {
                        if (artistServices.id == Integer.parseInt(staffServices.artistServiceId)) {
                            artistServices.isSelected = true;
                            artistServices.edtCompletionTime = staffServices.completionTime;
                            artistServices.edtBookingType = staffServices.bookingType;
                            artistServices.edtInCallPrice = Double.parseDouble(staffServices.inCallPrice);

                            if (staffServices.outCallPrice.equals("0") && artistServices.outCallPrice!=0) {
                                artistServices.isOutCallEdited = "0";
                                artistServices.edtOutCallPrice = Double.parseDouble(staffServices.outCallPrice);
                            } else {
                                artistServices.edtOutCallPrice = Double.parseDouble(staffServices.outCallPrice);
                                artistServices.isOutCallEdited = "1";
                            }

                            if (staffServices.inCallPrice.equals("0") && artistServices.inCallPrice!=0) {
                                artistServices.isInCallEdited = "0";
                                artistServices.edtInCallPrice = Double.parseDouble(staffServices.inCallPrice);
                            }
                            else {
                                artistServices.edtInCallPrice = Double.parseDouble(staffServices.inCallPrice);
                                artistServices.isInCallEdited = "1";
                            }

                            localServiceMap.put(String.valueOf(artistServices.id),artistServices);

                        }
                    }
                }
            }
            //}
        }
        servicesListAdapter.notifyDataSetChanged();

        if (tempServicesList.size()!=0) {
            rvServices.setVisibility(View.VISIBLE);
            tvNoRecord.setVisibility(View.GONE);
        }
        else {
            rvServices.setVisibility(View.GONE);
            tvNoRecord.setVisibility(View.VISIBLE);
        }

        //if (tempServicesList.size()==0)
        // apiForGetService();
    }

    private void showAlertDailog(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddStaffServicesActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure want to discard changes?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                //selected.clear();
                localServiceMap.clear();
                dialog.cancel();
                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivHeaderBack:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                onBackPressed();
                break;

            case R.id.btnContinue:
                if (localServiceMap.size()!=0){
                    ArrayList<String> sIds = new ArrayList<>();
                    // String[] sIds = new String[localMap.size()];
                    int i = 0;

                    ArrayList<SelectedServices> listOfValues = new ArrayList<>();
                    for(Map.Entry<String, Services> entry : localServiceMap.entrySet()){
                        Services  services = entry.getValue();
                        sIds.add(String.valueOf(services.id));

                        SelectedServices selectedServices = new SelectedServices();
                        selectedServices.serviceId = String.valueOf(services.serviceId);
                        selectedServices.serviceName = services.bizTypeName;
                        selectedServices.subServiceName = services.subserviceName;
                        selectedServices.subserviceId = String.valueOf(services.subserviceId);
                        selectedServices.artistId = staffDetail.staffId;
                        selectedServices.businessId = String.valueOf(user.id);
                        selectedServices.artistServiceId = String.valueOf(services.id);
                        selectedServices.inCallPrice = String.valueOf(services.inCallPrice);
                        selectedServices.outCallPrice = String.valueOf(services.outCallPrice);
                        selectedServices.completionTime = services.completionTime;
                        selectedServices.title = services.serviceName;
                        selectedServices._id = String.valueOf(services.id);
                        selectedServices.isHold = false;
                        listOfValues.add(selectedServices);

                        i++;
                    }
                    // Collection<company_services> values = localServiceMap.values();
                    //ArrayList<company_services> listOfValues = new ArrayList<>(values);


                    Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                    String jsonString = gson.toJson(listOfValues);

                    if (!jsonString.isEmpty()) {
                        apiForAddServices(jsonString,sIds);
                    }
                }else {
                    MyToast.getInstance(AddStaffServicesActivity.this).showDasuAlert("Please select staff service");
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            switch (requestCode){
                case 20:
                    Services services = (Services) data.getSerializableExtra("service");
                    localServiceMap.put(String.valueOf(services.id),services);
                    for (int i=0;i<tempServicesList.size();i++){
                        if (tempServicesList.get(i).id==services.id) {
                            Services newService = tempServicesList.get(i);
                            newService.isSelected = true;
                            newService.edtCompletionTime = services.completionTime;
                            newService.edtInCallPrice = services.inCallPrice;
                            newService.edtOutCallPrice = services.outCallPrice;
                            newService.edtBookingType = services.bookingType;
                            servicesListAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    break;
            }
        }
    }

    private void apiForAddServices(final String jsonArray,final ArrayList<String> sIds){
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddStaffServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForAddServices(jsonArray,sIds);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staffDetail.staffId);
        params.put("businessId", String.valueOf(user.id));
        params.put("staffService", jsonArray);

        HttpTask task = new HttpTask(new HttpTask.Builder(AddStaffServicesActivity.this, "addStaffService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        JSONArray staffServiceArray = js.getJSONArray("staffServices");
                        for (int i=0; i<staffServiceArray.length(); i++) {
                            JSONObject object = staffServiceArray.getJSONObject(i);
                            if (localServiceMap.size()!=0){
                                for(Map.Entry<String, Services> entry : localServiceMap.entrySet()){
                                    Services  selectedServices = entry.getValue();
                                    if (object.getString("artistServiceId").equals(String.valueOf(selectedServices.id))) {
                                        selectedServices.id = Integer.parseInt(object.getString("_id"));;
                                        //   selectedServices.businessId = object.getString("businessId");;
                                        //   selectedServices.artistId = object.getString("artistId");;
                                        selectedServices.id = Integer.parseInt(object.getString("artistServiceId"));;
                                        selectedServices.subserviceId = Integer.parseInt(object.getString("subserviceId"));;
                                        selectedServices.serviceId = Integer.parseInt(object.getString("serviceId"));;
                                        selectedServices.inCallPrice = Double.parseDouble(object.getString("inCallPrice"));;
                                        selectedServices.outCallPrice = Double.parseDouble(object.getString("outCallPrice"));;
                                        selectedServices.completionTime = object.getString("completionTime");;
                                    }
                                }
                            }

                            //    Gson gson = new Gson();
                            //    AddedStaffServices item3 = gson.fromJson(String.valueOf(object), AddedStaffServices.class);
                            //    staffDetail.staffServices.add(item3);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("jsonArray", sIds);
                        intent.putExtra("staffDetail", staffDetail);
                        setResult(RESULT_OK, intent);
                        finish();

                    }else {
                        if (!message.equals("ArtistCategory already added"))
                            MyToast.getInstance(AddStaffServicesActivity.this).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(AddStaffServicesActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
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

    private void apiForDeleteBookedService(final Services selectedServices){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddStaffServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteBookedService(selectedServices);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("addServiceId", String.valueOf(selectedServices.id));
        /*if (!selectedServices.isHold)
            params.put("addServiceId", selectedServices._id);
        else
            params.put("addServiceId", "");*/
        // params.put("userId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(AddStaffServicesActivity.this, "deleteStaffService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        // selectedServices.isHold = false;
                        selectedServices.edtCompletionTime = "";
                        selectedServices.edtOutCallPrice = 0.0;
                        selectedServices.edtInCallPrice = 0.0;
                        selectedServices.isSelected = false;
                        selectedServices.isInCallEdited = "";
                        selectedServices.isOutCallEdited = "";
                        servicesListAdapter.notifyDataSetChanged();

                        if (staffDetail.staffServices.size()!=0){
                            for (AddedStaffServices staffServices : staffDetail.staffServices){
                                if (staffServices.artistServiceId.equals(String.valueOf(selectedServices.id))) {
                                    staffDetail.staffServices.remove(staffServices);
                                    break;
                                }
                            }
                        }

                        if (localServiceMap.size()==0 ){
                            localServiceMap.clear();
                            staffDetail.staffServices.clear();
                            finish();

                        }else{
                            localServiceMap.remove(String.valueOf(selectedServices.id));
                        }
                        MyToast.getInstance(AddStaffServicesActivity.this).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(AddStaffServicesActivity.this).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(AddStaffServicesActivity.this);
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
        if (localServiceMap.size() > 0) {
            showAlertDailog();
        }else
            super.onBackPressed();
    }
}
