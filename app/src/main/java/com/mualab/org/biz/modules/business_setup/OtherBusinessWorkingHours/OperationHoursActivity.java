package com.mualab.org.biz.modules.business_setup.OtherBusinessWorkingHours;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.business_setup.OtherBusinessWorkingHours.adapter.CompanyOperationHoursAdapter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OperationHoursActivity extends AppCompatActivity implements View.OnClickListener {
    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_working_hours);
        init();
    }

    private void init(){
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser();
        //  bpSession  = Mualab.getInstance().getBusinessProfileSession();
        //  businessDays =  getBusinessdays();
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        List<BusinessDay> businessDays = (List<BusinessDay>) args.getSerializable("workingHours");
        // private PreRegistrationSession bpSession;
        //  List<BusinessDay> businessDays = companyDetail.businessDays;
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        ivKoobiLogo.setVisibility(View.GONE);
        ImageView iv_back = findViewById(R.id.iv_back);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        TextView tvNoDataFound = findViewById(R.id.tvNoDataFound);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        tvHeaderText.setVisibility(View.VISIBLE);
        tvHeaderText.setText("Operation Hours");

        assert businessDays != null;
        Collections.sort(businessDays, new Comparator<BusinessDay>() {

            @Override
            public int compare(BusinessDay a1, BusinessDay a2) {
                Long long1 = (long) a1.dayId;
                Long long2 = (long) a2.dayId;
                return long1.compareTo(long2);
              /*  if (a1.dayId == 0 || a2.dayId == 0)
                    return -1;
                else {
                    Long long1 = (long) a1.dayId;
                    Long long2 = (long) a2.dayId;
                    return long1.compareTo(long2);
                }*/
            }
        });

        if (businessDays.size() == 0)
            tvNoDataFound.setVisibility(View.VISIBLE);
        else
            tvNoDataFound.setVisibility(View.GONE);

        CompanyOperationHoursAdapter adapter = new CompanyOperationHoursAdapter(OperationHoursActivity.this, businessDays);
        RecyclerView rvBusinessDay = findViewById(R.id.rvBusinessDay);
        rvBusinessDay.setLayoutManager(new LinearLayoutManager(OperationHoursActivity.this));
        rvBusinessDay.setAdapter(adapter);


       /* int serviceType = bpSession.getServiceType();
        if (serviceType==1){

        }else if (serviceType==2){

        }else if (serviceType==3){
        }*/

        iv_back.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;

            case R.id.btnContinue:
/*
                if(isvalidBusinessHours()){
                    if (!ConnectionDetector.isConnected()) {
                        new NoConnectionDialog(OperationHoursActivity.this, new NoConnectionDialog.Listner() {
                            @Override
                            public void onNetworkChange(Dialog dialog, boolean isConnected) {
                                if(isConnected){
                                    dialog.dismiss();
                                    bpSession.setBusinessHours(businessDays);
                                    updateDataIntoServerDb();
                                }
                            }
                        }).show();
                    }else {
                        bpSession.setBusinessHours(businessDays);
                        updateDataIntoServerDb();
                    }

                }*/

                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
