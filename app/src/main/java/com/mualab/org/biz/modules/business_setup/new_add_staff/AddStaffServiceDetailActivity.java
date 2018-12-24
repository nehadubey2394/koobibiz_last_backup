package com.mualab.org.biz.modules.business_setup.new_add_staff;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.modules.profile.db_modle.Services;
import com.mualab.org.biz.session.PreRegistrationSession;

public class AddStaffServiceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = AddStaffServiceDetailActivity.class.getName();
    private String sInCallPrice="",sOutCallPrice="",
            sBookingType="",complieTime="";
    private long mLastClickTime = 0;
    private Services services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_service_detail);
        initView();
    }

    private void initView() {
        // if(mContext instanceof AddNewStaffActivity) {
        //   ((AddNewStaffActivity) mContext).setTitle();
        services = (Services) getIntent().getSerializableExtra("serviceItem");

        if (services.isSelected){
            sBookingType = services.edtBookingType;
            complieTime = services.edtCompletionTime;

            if (services.edtInCallPrice!=0)
                sInCallPrice = String.valueOf(services.edtInCallPrice);

            if (services.edtOutCallPrice!=0)
                sOutCallPrice = String.valueOf(services.edtOutCallPrice);
        }else {
            sBookingType = services.bookingType;
            complieTime = services.completionTime;

            if (services.inCallPrice!=0)
                sInCallPrice = String.valueOf(services.inCallPrice);

            if (services.outCallPrice!=0)
                sOutCallPrice = String.valueOf(services.outCallPrice);

        }

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_services));

        RelativeLayout rlPrice = findViewById(R.id.rlPrice);
        RelativeLayout rlBookingType = findViewById(R.id.rlBookingType);
        RelativeLayout rlComplitionTime = findViewById(R.id.rlComplitionTime);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);

        TextView tvServiceName = findViewById(R.id.tvServiceName);
        tvServiceName.setText(services.serviceName);

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        int serviceType = pSession.getServiceType();

       /* if (serviceType == 3) {
            rlBookingType.setVisibility(View.VISIBLE);
        } else
            rlBookingType.setVisibility(View.GONE);
*/
        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                finish();
            }
        });

        rlPrice.setOnClickListener(this);
        rlComplitionTime.setOnClickListener(this);
        rlBookingType.setOnClickListener(this);
        btnContinue.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;

            case R.id.btnContinue:
                vailidateService();
                break;

            case R.id.rlCategory:
                break;


            case R.id.rlPrice:
                Intent intent2 = new Intent(AddStaffServiceDetailActivity.this, ChangeServiceDetailActivity.class);
                intent2.putExtra("inCallPrice", sInCallPrice);
                intent2.putExtra("outCallPrice", sOutCallPrice);
                intent2.putExtra("bookingType", sBookingType);
                intent2.putExtra("keyField", "price");
                intent2.putExtra("service", services);
                intent2.putExtra("commingFrom", "AddStaffServiceDetailActivity");
                startActivityForResult(intent2, 20);
                break;

            case R.id.rlBookingType:
                Intent intent3 = new Intent(AddStaffServiceDetailActivity.this, ChangeServiceDetailActivity.class);
                intent3.putExtra("bookingType", sBookingType);
                intent3.putExtra("service", services);
                intent3.putExtra("keyField", "bookingType");
                intent3.putExtra("commingFrom", "AddStaffServiceDetailActivity");
                startActivityForResult(intent3, 30);
                break;

            case R.id.rlComplitionTime:
                Intent intent5 = new Intent(AddStaffServiceDetailActivity.this, ChangeServiceDetailActivity.class);
                intent5.putExtra("complieTime", complieTime);
                intent5.putExtra("keyField", "complieTime");
                intent5.putExtra("service", services);
                intent5.putExtra("commingFrom", "AddStaffServiceDetailActivity");
                startActivityForResult(intent5, 50);
                // showPicker(getString(R.string.time_for_completion));
                break;
        }
    }

    private void vailidateService(){

        // services.bookingType = sBookingType;
        // services.completionTime = complieTime;

        if (!sInCallPrice.isEmpty())
            services.inCallPrice = Double.parseDouble(sInCallPrice);
        else
            services.inCallPrice = 0;

        if (!sOutCallPrice.isEmpty())
            services.outCallPrice = Double.parseDouble(sOutCallPrice);
        else
            services.outCallPrice = 0;

        services.bookingType = sBookingType;

        if (!complieTime.equals("")){

            services.edtCompletionTime = complieTime;
            services.completionTime = complieTime;

            switch (sBookingType) {

                case "Incall":
                    if (!sInCallPrice.isEmpty()) {
                        services.edtBookingType = sBookingType;
                        services.inCallPrice = Double.parseDouble(sInCallPrice);
                        services.edtInCallPrice = Double.parseDouble(sInCallPrice);
                        services.isSelected = true;
                        sendIntentResult();
                    } else {
                        MyToast.getInstance(AddStaffServiceDetailActivity.this).showDasuAlert("Please enter service price");
                    }
                    break;
                case "Outcall":
                    if (!sOutCallPrice.isEmpty()) {
                        services.edtBookingType = sBookingType;
                        services.outCallPrice = Double.parseDouble(sOutCallPrice);
                        services.edtOutCallPrice = Double.parseDouble(sOutCallPrice);
                        services.isSelected = true;
                        sendIntentResult();
                    } else {
                        MyToast.getInstance(AddStaffServiceDetailActivity.this).showDasuAlert("Please enter service price");
                    }

                    break;
                case "Both":
                    if (!sInCallPrice.isEmpty() && !sOutCallPrice.isEmpty()) {
                        services.edtBookingType = sBookingType;
                        services.outCallPrice = Double.parseDouble(sOutCallPrice);
                        services.edtOutCallPrice = Double.parseDouble(sOutCallPrice);
                        services.inCallPrice = Double.parseDouble(sInCallPrice);
                        services.edtInCallPrice = Double.parseDouble(sInCallPrice);
                        services.isSelected = true;
                        sendIntentResult();
                    } else {
                        MyToast.getInstance(AddStaffServiceDetailActivity.this).showDasuAlert("Please enter service price");
                    }
                    break;
            }

        }else {
            MyToast.getInstance(AddStaffServiceDetailActivity.this).showDasuAlert("Please select compilation time for" +
                    " service");
        }


    }

    private void sendIntentResult(){
        Intent intent = new Intent();
        intent.putExtra("service", services);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && resultCode != 0){
            switch (requestCode){
                case 20:
                    sBookingType =  data.getStringExtra("bookingType");
                    switch (sBookingType) {
                        case "Incall":
                            if (data.hasExtra("inCallPrice")) {
                                sInCallPrice = data.getStringExtra("inCallPrice");
                                sOutCallPrice="";
                            }
                            break;
                        case "Outcall":
                            if (data.hasExtra("outCallPrice")) {
                                sOutCallPrice = data.getStringExtra("outCallPrice");
                                sInCallPrice="";
                            }
                            break;
                        case "Both":
                            if (data.hasExtra("inCallPrice")) {
                                sInCallPrice = data.getStringExtra("inCallPrice");
                            }
                            if (data.hasExtra("outCallPrice")) {
                                sOutCallPrice = data.getStringExtra("outCallPrice");
                            }
                            break;
                    }

                    break;
                case 30:
                    sBookingType =  data.getStringExtra("bookingType");
                    sOutCallPrice =  "";
                    sInCallPrice =  "";
                    break;

                case 50:
                    complieTime =  data.getStringExtra("complieTime");
                    break;
            }
        }
    }
}
