package com.mualab.org.biz.modules.profile_setup.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EditServiceActivity extends AppCompatActivity implements
        View.OnClickListener {
    private EditText etServiceName,etServiceDesc;
    private String sServoiceName="",sInCallPrice="",sOutCallPrice="",sServiceDesc="",
            sBookingType="",complieTime="";
    private TextView tvPrice,tvBookingType,tvTime;
    private Services services;
    private List<Services>tempServicesList,servicesList;
    private ProgressBar pbLoder;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_service);
        tempServicesList = new ArrayList<>();
        servicesList = new ArrayList<>();

        pbLoder = findViewById(R.id.pbLoder);

        services = (Services) getIntent().getSerializableExtra("serviceItem");

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

        getAllServiceData();

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
                if (tempServicesList.size()!=0){
                    sServoiceName = etServiceName.getText().toString().trim();
                    if (!sServoiceName.equals("") ){
                        for (int i=0;i<tempServicesList.size();i++) {
                            if (!sServoiceName.equals(services.serviceName)){
                                Services services1 = tempServicesList.get(i);
                                if (services1.serviceName.equalsIgnoreCase(sServoiceName)) {
                                    isAlreadyAdded = true;
                                    break;
                                }
                            }
                        }

                        if (isAlreadyAdded) {
                            MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Service already exist");
                        }else
                            vailidateService();
                    }else {
                        MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please enter service name");
                    }
                }

                break;

            case R.id.rlPrice:
                Intent intent2 = new Intent(EditServiceActivity.this,AddServiceFieldsActivity.class);
                intent2.putExtra("inCallPrice",sInCallPrice);
                intent2.putExtra("outCallPrice",sOutCallPrice);
                intent2.putExtra("bookingType",sBookingType);
                intent2.putExtra("keyField","price");
                startActivityForResult(intent2,20);
                break;

            case R.id.rlBookingType:
                Intent intent3 = new Intent(EditServiceActivity.this,AddServiceFieldsActivity.class);
                intent3.putExtra("bookingType",sBookingType);
                intent3.putExtra("keyField","bookingType");
                startActivityForResult(intent3,30);
                break;


            case R.id.rlComplitionTime:
                complieTime = tvTime.getText().toString().trim();
                Intent intent5 = new Intent(EditServiceActivity.this,AddServiceFieldsActivity.class);
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
                                updateServices(tempService);
                            } else {
                                MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please enter service price");
                            }
                            break;
                        case "Outcall":
                            if (tempService.outCallPrice != 0.0) {
                                updateServices(tempService);
                            } else {
                                MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please enter service price");
                            }

                            break;
                        case "Both":
                            if (tempService.inCallPrice != 0.0 && tempService.outCallPrice != 0.0) {
                                updateServices(tempService);
                            } else {
                                MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please enter service price");
                            }
                            break;

                    }

                }else {
                    MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please select compilation time for" +
                            " service");
                }

            }else {
                MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please enter service description");
            }

        }else {
            MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Please enter service name");
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

                        pbLoder.setVisibility(View.GONE);

                        shortList();

                    }
                });

            }
        }).start();


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

    public void updateServices(final Services services) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Mualab.get().getDB().businessTypeDao().insertAll(tempBizypeList);
                //  Mualab.get().getDB().categoryDao().insertAll(tempCategoryList);
                Mualab.get().getDB().serviceDao().update(services);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.getInstance(EditServiceActivity.this).showDasuAlert("Service updated successfully");
                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }
        }).start();

    }
}
