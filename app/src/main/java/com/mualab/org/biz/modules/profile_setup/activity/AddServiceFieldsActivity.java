package com.mualab.org.biz.modules.profile_setup.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.session.PreRegistrationSession;

import java.util.ArrayList;

import views.pickerview.MyTimePickerDialog;
import views.pickerview.timepicker.TimePicker;

public class AddServiceFieldsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etServiceName,etOutCallPrice,etInCallPrice,etServiceDesc;
    private String keyField = "",inCallPrice,outCallPrice,serviceName,serviceDesc,
            bookingType,complieTime,commingFrom;
    private TextView tvBookingType,tvCompletionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_fields);
        keyField = getIntent().getStringExtra("keyField");
        bookingType = getIntent().getStringExtra("bookingType");
        commingFrom = getIntent().getStringExtra("commingFrom");

        initView();
    }

    private void initView(){
        etServiceName = findViewById(R.id.etServiceName);
        etOutCallPrice = findViewById(R.id.etOutCallPrice);
        etServiceDesc = findViewById(R.id.etServiceDesc);
        etInCallPrice = findViewById(R.id.etInCallPrice);
        tvBookingType = findViewById(R.id.tvBookingType);
        tvCompletionTime = findViewById(R.id.tvCompletionTime);

        LinearLayout llInCallPrice = findViewById(R.id.llInCallPrice);
        LinearLayout llOutCallPrice = findViewById(R.id.llOutCallPrice);
        LinearLayout llServiceDesc = findViewById(R.id.llServiceDesc);
        LinearLayout llServiceName = findViewById(R.id.llServiceName);
        RelativeLayout rlBusinessType = findViewById(R.id.rlBusinessType);
        RelativeLayout rlComplitionTime = findViewById(R.id.rlComplitionTime);

        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        ImageView iv_back = findViewById(R.id.iv_back);

        if (commingFrom.equals("AddStaffServiceDetailActivity"))
            tvHeaderText.setText(getString(R.string.text_services));
        else
            tvHeaderText.setText(getString(R.string.add_services));

        tvHeaderText.setVisibility(View.VISIBLE);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        BusinessProfile bsp = pSession.getBusinessProfile();
        int serviceType = pSession.getServiceType();

        final ArrayList<String>arrayList = new ArrayList<>();

        if (serviceType==3){
            arrayList.add("Both");
            arrayList.add("Incall");
            arrayList.add("Outcall");
        }else if (serviceType==2)
            arrayList.add("Outcall");
        else if (serviceType==1)
            arrayList.add("Incall");

       /* for (int i = 0;i<arrayList.size();i++){
            if (arrayList.get(i).equals(String.valueOf(bsp.serviceType)))
                tempList.add(0,arrayList.get(i));


        }*/
      /*  if (bookingType!=null && !bookingType.equals("")){
            switch (bookingType) {
                case "Incall":
                    arrayList.remove("Incall");
                    arrayList.add(0,"Incall");
                    break;
                case "Outcall":
                    arrayList.remove("Outcall");
                    arrayList.add(0,"Outcall");
                    break;
                case "Both":
                    arrayList.remove("Both");
                    arrayList.add(0,"Both");
                    break;
            }
        }*/

        final Spinner spBookingType = findViewById(R.id.spBookingType);
        ArrayAdapter arrayAdapter = new ArrayAdapter(AddServiceFieldsActivity.this
                ,R.layout.layout_spinner_items, arrayList);

        spBookingType.setAdapter(arrayAdapter);
        spBookingType.setPrompt("");
        spBookingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spBookingType.setPrompt("");
                TextView textView =  view.findViewById(R.id.tvSpItem);
                textView.setText("");
                bookingType = arrayList.get(i);
                tvBookingType.setVisibility(View.VISIBLE);
                tvBookingType.setText(arrayList.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // textv1.setText(getString(R.string.business_type));
                // tvBizType.setVisibility(View.GONE);
            }
        });

        switch (keyField) {
            case "serviceName":
                serviceName = getIntent().getStringExtra("serviceName");
                llInCallPrice.setVisibility(View.GONE);
                llOutCallPrice.setVisibility(View.GONE);
                llServiceDesc.setVisibility(View.GONE);
                llServiceName.setVisibility(View.VISIBLE);
                etServiceName.setText(serviceName);
                break;
            case "price":
                inCallPrice = getIntent().getStringExtra("inCallPrice");
                outCallPrice = getIntent().getStringExtra("outCallPrice");
                bookingType = getIntent().getStringExtra("bookingType");

                if (!bookingType.equals("")){
                    switch (bookingType) {
                        case "Incall":
                            llInCallPrice.setVisibility(View.VISIBLE);
                            llOutCallPrice.setVisibility(View.GONE);
                            etInCallPrice.setText(inCallPrice);
                            break;
                        case "Outcall":
                            llInCallPrice.setVisibility(View.GONE);
                            llOutCallPrice.setVisibility(View.VISIBLE);
                            etOutCallPrice.setText(outCallPrice);
                            break;
                        default:
                            etInCallPrice.setText(inCallPrice);
                            etOutCallPrice.setText(outCallPrice);
                            llInCallPrice.setVisibility(View.VISIBLE);
                            llOutCallPrice.setVisibility(View.VISIBLE);
                            break;
                    }
                }else {
                    if ( bsp.serviceType==1){
                        llInCallPrice.setVisibility(View.VISIBLE);
                        llOutCallPrice.setVisibility(View.GONE);
                        etInCallPrice.setText(inCallPrice);
                    }else if ( bsp.serviceType==2){
                        llInCallPrice.setVisibility(View.GONE);
                        llOutCallPrice.setVisibility(View.VISIBLE);
                        etOutCallPrice.setText(outCallPrice);
                    }else {
                        etInCallPrice.setText(inCallPrice);
                        etOutCallPrice.setText(outCallPrice);
                        llInCallPrice.setVisibility(View.VISIBLE);
                        llOutCallPrice.setVisibility(View.VISIBLE);
                    }
                }

                llServiceDesc.setVisibility(View.GONE);
                llServiceName.setVisibility(View.GONE);

                break;
            case "serviceDesc":
                serviceDesc = getIntent().getStringExtra("serviceDesc");
                etServiceDesc.setText(serviceDesc);
                llInCallPrice.setVisibility(View.GONE);
                llOutCallPrice.setVisibility(View.GONE);
                llServiceDesc.setVisibility(View.VISIBLE);
                llServiceName.setVisibility(View.GONE);
                break;
            case "bookingType":
                bookingType = getIntent().getStringExtra("bookingType");
                llInCallPrice.setVisibility(View.GONE);
                llOutCallPrice.setVisibility(View.GONE);
                llServiceDesc.setVisibility(View.GONE);
                llServiceName.setVisibility(View.GONE);
                rlBusinessType.setVisibility(View.VISIBLE);
                break;

            case "complieTime":
                complieTime = getIntent().getStringExtra("complieTime");
                tvCompletionTime.setText(complieTime);
                llInCallPrice.setVisibility(View.GONE);
                llOutCallPrice.setVisibility(View.GONE);
                llServiceDesc.setVisibility(View.GONE);
                llServiceName.setVisibility(View.GONE);
                rlBusinessType.setVisibility(View.GONE);
                rlComplitionTime.setVisibility(View.VISIBLE);
                break;
        }
        iv_back.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        rlComplitionTime.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.rlComplitionTime:
                // showPicker(getString(R.string.time_for_completion));
                showPicker();
                break;

            case R.id.btnContinue:
                Intent intent = new Intent();
                intent.putExtra("keyField", keyField);

                switch (keyField) {
                    case "serviceName":
                        serviceName = etServiceName.getText().toString().trim();
                        if (!serviceName.isEmpty()) {
                            intent.putExtra("serviceName", serviceName);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                            MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter service name");

                        break;

                    case "price":
                        switch (bookingType) {
                            case "Incall":
                                inCallPrice = etInCallPrice.getText().toString().trim();
                                if (!inCallPrice.isEmpty() && !inCallPrice.equals(".") && !inCallPrice.equals("..")){
                                    double incallP = Double.parseDouble(inCallPrice);
                                    if (incallP!=0) {
                                        intent.putExtra("inCallPrice", inCallPrice);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else
                                        MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter incall price");

                                }else
                                    MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter incall price");

                                break;
                            case "Outcall":
                                outCallPrice = etOutCallPrice.getText().toString().trim();
                                if (!outCallPrice.isEmpty() && !outCallPrice.equals(".") && !outCallPrice.equals("..")){
                                    double inoutP = Double.parseDouble(outCallPrice);
                                    if (inoutP!=0) {
                                        intent.putExtra("outCallPrice", outCallPrice);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else
                                        MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter outcall price");

                                }else
                                    MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter outcall price");


                                break;
                            default:
                                outCallPrice = etOutCallPrice.getText().toString().trim();
                                inCallPrice = etInCallPrice.getText().toString().trim();

                                if (outCallPrice.isEmpty() || inCallPrice.isEmpty())
                                    MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter service price");
                                else {
                                    intent.putExtra("outCallPrice", outCallPrice);
                                    intent.putExtra("inCallPrice", inCallPrice);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                                break;
                        }

                        break;
                    case "serviceDesc":
                        serviceDesc = etServiceDesc.getText().toString().trim();
                        if (!serviceDesc.isEmpty()) {
                            intent.putExtra("serviceDesc", serviceDesc);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                            MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please enter service description");
                        break;

                    case "bookingType":
                        bookingType = tvBookingType.getText().toString().trim();
                        if (!bookingType.isEmpty()) {
                            intent.putExtra("bookingType", bookingType);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                            MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please select booking type");

                        break;
                    case "complieTime":
                        complieTime = tvCompletionTime.getText().toString().trim();
                        if (!complieTime.isEmpty() && !complieTime.equals("00:00")) {
                            intent.putExtra("complieTime", complieTime);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        else
                            MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Please select completion time");

                        break;
                }

                break;
        }
    }

   /* public void showPicker( final String title){
        int hours = 01;
        //int minute = 00;
        String tmpTime = tvCompletionTime.getText().toString();
        *//*if(!tmpTime.equals("HH:MM")){
            String[] arrayTime = tmpTime.split(":");
            hours = Integer.parseInt(arrayTime[0]);
            // minute = Integer.parseInt(arrayTime[1]);
        }*//*
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(AddServiceFieldsActivity.this, new MyTimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hours, int minute) {
                tvCompletionTime.setVisibility(View.VISIBLE);
                tvCompletionTime.setText(String.format("%s:%s", String.format("%02d", hours), String.format("%02d", minute)));
            }
        }, title, hours, 00,10);


        mTimePicker.show();
    }*/

    public void showPicker(){
        int hours = 01;

       /* MyTimePickerDialog mTimePicker = new MyTimePickerDialog(AddServiceFieldsActivity.this, new MyTimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hours, int minute) {
                tvCompletionTime.setText(String.format("%s:%s", String.format("%02d", hours), String.format("%02d", minute)));

            }
        }, getString(R.string.time_for_completion), hours, 00, 10, 3);
        mTimePicker.show();*/
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(AddServiceFieldsActivity.this,
                new MyTimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hours, int minute) {
                        if (hours>3){
                            MyToast.getInstance(AddServiceFieldsActivity.this).showDasuAlert("Maximum break time limit is 3:00 hours");
                        }else {
                            tvCompletionTime.setText(String.format("%s:%s", String.format("%02d", hours), String.format("%02d", minute)));
                        }

                    }
                }, "", hours, 00,10);

        mTimePicker.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
