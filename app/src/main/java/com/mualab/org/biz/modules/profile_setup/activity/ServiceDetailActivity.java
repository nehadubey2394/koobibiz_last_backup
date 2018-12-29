package com.mualab.org.biz.modules.profile_setup.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.session.PreRegistrationSession;

public class ServiceDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);
        initView();
    }

    private void initView(){
        Services services = (Services) getIntent().getSerializableExtra("serviceItem");
        TextView tvServiceName = findViewById(R.id.tvServiceName);
        TextView tvOutCallPrice = findViewById(R.id.tvOutCallPrice);
        TextView tvServiceDesc = findViewById(R.id.tvServiceDesc);
        TextView tvInCallPrice = findViewById(R.id.tvInCallPrice);
        TextView tvCompileTime = findViewById(R.id.tvCompileTime);
        TextView tvBusinessTypeName = findViewById(R.id.tvBusinessTypeName);
        TextView tvCategoryName = findViewById(R.id.tvCategoryName);
        TextView tvBookingType = findViewById(R.id.tvBookingType);
        LinearLayout llInCallPrice = findViewById(R.id.llInCallPrice);
        LinearLayout llOutCallPrice = findViewById(R.id.llOutCallPrice);
        LinearLayout llServiceDesc = findViewById(R.id.llServiceDesc);
        LinearLayout llServiceName = findViewById(R.id.llServiceName);
        LinearLayout llBookingType = findViewById(R.id.llBookingType);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        tvHeaderText.setVisibility(View.VISIBLE);
        ImageView iv_back = findViewById(R.id.iv_back);
        tvHeaderText.setText("Service Details");

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AppCompatButton btnContinue = findViewById(R.id.btnContinue);

        //  PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        // BusinessProfile bsp = pSession.getBusinessProfile();

        if (services!=null){
            tvServiceName.setText(services.serviceName);
            tvOutCallPrice.setText("£"+services.outCallPrice);
            tvInCallPrice.setText("£"+services.inCallPrice);
            tvServiceDesc.setText(services.description);
            tvCompileTime.setText(services.completionTime);
            tvBusinessTypeName.setText(services.bizTypeName);
            tvCategoryName.setText(services.subserviceName);


            if (services.bookingType.equals("Incall")){
                llInCallPrice.setVisibility(View.VISIBLE);
                llOutCallPrice.setVisibility(View.GONE);
                tvBookingType.setText("Incall");
            }else if (services.bookingType.equals("Outcall")){
                llInCallPrice.setVisibility(View.GONE);
                llOutCallPrice.setVisibility(View.VISIBLE);
                tvBookingType.setText("Outcall");
            }else {
                llInCallPrice.setVisibility(View.VISIBLE);
                llOutCallPrice.setVisibility(View.VISIBLE);
                tvBookingType.setText("Both");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
