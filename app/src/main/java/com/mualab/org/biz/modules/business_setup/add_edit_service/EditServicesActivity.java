package com.mualab.org.biz.modules.business_setup.add_edit_service;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.profile_setup.activity.AddServiceFieldsActivity;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditServicesActivity extends AppCompatActivity implements
        View.OnClickListener {
    private EditText etServiceName,etServiceDesc;
    private String sServoiceName="",sInCallPrice="",sOutCallPrice="",sServiceDesc="",
            sBookingType="",complieTime="";
    private TextView tvPrice,tvBookingType,tvTime;
    private Services services;
    private List<Services>servicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);

        Bundle args = getIntent().getBundleExtra("bundle");
        servicesList = (ArrayList<Services>) args.getSerializable("servicesList");
        services = (Services) args.getSerializable("serviceItem");

        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        tvHeaderText.setVisibility(View.VISIBLE);
        ImageView iv_back = findViewById(R.id.iv_back);
        tvHeaderText.setText("Edit Services");
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);

        tvPrice = findViewById(R.id.tvPrice);
        tvBookingType = findViewById(R.id.tvBookingType);
        tvTime = findViewById(R.id.tvTime);
        etServiceName = findViewById(R.id.etServiceName);
        etServiceDesc = findViewById(R.id.etServiceDesc);
        RelativeLayout rlPrice = findViewById(R.id.rlPrice);
        RelativeLayout rlBookingType = findViewById(R.id.rlBookingType);
        RelativeLayout rlComplitionTime = findViewById(R.id.rlComplitionTime);

        if (services!=null){
            etServiceName.setText(services.serviceName);
            etServiceDesc.setText(services.description);
            tvBookingType.setText(services.bookingType);
            tvTime.setText(services.completionTime);
            sBookingType = services.bookingType;

            if(sBookingType.equals("Incall")){
                if (services.inCallPrice!=0) {
                    tvPrice.setText("£" + services.inCallPrice);
                    sInCallPrice = String.valueOf(services.inCallPrice);
                }
            }
            if(sBookingType.equals("Outcall")){
                if (services.outCallPrice!=0) {
                    tvPrice.setText("£" + services.outCallPrice);
                    sOutCallPrice = String.valueOf(services.outCallPrice);
                }
            }
            if(sBookingType.equals("Both")){
                sInCallPrice = String.valueOf(services.inCallPrice);
                sOutCallPrice = String.valueOf(services.outCallPrice);
                tvPrice.setText("£" + services.inCallPrice);
            }


        }

        rlBookingType.setOnClickListener(this);
        rlPrice.setOnClickListener(this);
        rlComplitionTime.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        boolean isAlreadyAdded = false;

        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.btnContinue:
                if (servicesList.size()!=0){
                    sServoiceName = etServiceName.getText().toString().trim();
                    if (!sServoiceName.equals("") ){
                        for (int i=0;i<servicesList.size();i++) {
                            if (!sServoiceName.equals(services.serviceName)){
                                Services services1 = servicesList.get(i);
                                if (services1.serviceName.equalsIgnoreCase(sServoiceName)) {
                                    isAlreadyAdded = true;
                                    break;
                                }
                            }
                        }

                        if (isAlreadyAdded) {
                            MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Service already exist");
                        }else
                            vailidateService();
                    }else {
                        MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please enter service name");
                    }
                }

                break;

            case R.id.rlPrice:
                Intent intent2 = new Intent(EditServicesActivity.this,AddServiceFieldsActivity.class);
                intent2.putExtra("inCallPrice",sInCallPrice);
                intent2.putExtra("outCallPrice",sOutCallPrice);
                intent2.putExtra("bookingType",sBookingType);
                intent2.putExtra("keyField","price");
                startActivityForResult(intent2,20);
                break;

            case R.id.rlBookingType:
                Intent intent3 = new Intent(EditServicesActivity.this,AddServiceFieldsActivity.class);
                intent3.putExtra("bookingType",sBookingType);
                intent3.putExtra("keyField","bookingType");
                startActivityForResult(intent3,30);
                break;


            case R.id.rlComplitionTime:
                complieTime = tvTime.getText().toString().trim();
                Intent intent5 = new Intent(EditServicesActivity.this,AddServiceFieldsActivity.class);
                intent5.putExtra("complieTime",complieTime);
                intent5.putExtra("keyField","complieTime");
                startActivityForResult(intent5,50);
                // showPicker(getString(R.string.time_for_completion));
                break;
        }
    }

    private void vailidateService(){
        Services tempService = new Services();
        tempService.subserviceName = services.subserviceName;
        tempService.subserviceId = services.subserviceId;
        tempService.id = services.id;
        tempService.serviceName = etServiceName.getText().toString().trim();
        tempService.description = etServiceDesc.getText().toString().trim();
        tempService.completionTime = tvTime.getText().toString().trim();

        if (!sInCallPrice.isEmpty())
            tempService.inCallPrice = Double.parseDouble(sInCallPrice);
        else
            tempService.inCallPrice = 0;

        if (!sOutCallPrice.isEmpty())
            tempService.outCallPrice = Double.parseDouble(sOutCallPrice);
        else
            tempService.outCallPrice = 0;

        tempService.businessType = services.businessType;
        tempService.serviceId = services.serviceId;
        tempService.bizTypeName = services.bizTypeName;
        tempService.bookingType = sBookingType;

        if (!tempService.serviceName.equals("")){

            if (! tempService.description.equals("")){

                if (!tempService.completionTime.equals("")){

                    switch (sBookingType) {
                        case "Incall":
                            if (tempService.inCallPrice != 0.0) {
                                apiEditService(tempService);
                            } else {
                                MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please enter service price");
                            }
                            break;
                        case "Outcall":
                            if (tempService.outCallPrice != 0.0) {
                                apiEditService(tempService);
                            } else {
                                MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please enter service price");
                            }

                            break;
                        case "Both":
                            if (tempService.inCallPrice != 0.0 && tempService.outCallPrice != 0.0) {
                                apiEditService(tempService);
                            } else {
                                MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please enter service price");
                            }
                            break;

                    }

                }else {
                    MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please select compilation time for" +
                            " service");
                }

            }else {
                MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please enter service description");
            }

        }else {
            MyToast.getInstance(EditServicesActivity.this).showDasuAlert("Please enter service name");
        }

    }

    private void apiEditService(final Services services) {
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(EditServicesActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiEditService(services);
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
        body.put("id", String.valueOf(services.id));


        HttpTask task = new HttpTask(new HttpTask.Builder(EditServicesActivity.this,
                "addService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    Progress.hide(EditServicesActivity.this);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        setResult(RESULT_OK);
                        finish();

                    }else {
                        MyToast.getInstance(EditServicesActivity.this).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


                }catch (Exception e) {
                    Progress.hide(EditServicesActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Progress.hide(EditServicesActivity.this);
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(EditServicesActivity.this).showDasuAlert(helper.error_Messages(error));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            switch (requestCode){
                case 10:
                    sServoiceName =  data.getStringExtra("serviceName");
                    break;
                case 20:
                    if (data.hasExtra("outCallPrice")) {
                        sOutCallPrice = data.getStringExtra("outCallPrice");
                        tvPrice.setText("£"+sOutCallPrice);
                    }
                    if (data.hasExtra("inCallPrice")) {
                        sInCallPrice = data.getStringExtra("inCallPrice");
                        tvPrice.setText("£"+sInCallPrice);
                    }


                    break;
                case 30:
                    sBookingType =  data.getStringExtra("bookingType");
                    tvBookingType.setText(sBookingType);
                    tvPrice.setText("");
                    sOutCallPrice =  "";
                    sInCallPrice =  "";
                    break;
                case 40:
                    sServiceDesc =  data.getStringExtra("serviceDesc");
                    break;

                case 50:
                    complieTime =  data.getStringExtra("complieTime");
                    tvTime.setText(complieTime);
                    break;
            }
        }
    }

}
