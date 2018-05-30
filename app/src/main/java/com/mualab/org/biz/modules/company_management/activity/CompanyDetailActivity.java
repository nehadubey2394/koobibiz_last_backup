package com.mualab.org.biz.modules.company_management.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.booking.Company;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.company_management.adapter.AdapCompanyBusinessDays;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.util.StatusBarUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvJobTitle,tvSocialMedia,tvHoliday;
    private CompanyDetail companyDetail;
    private TextView tvUserName;
    private ImageView ivHeaderProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            companyDetail = (CompanyDetail) args.getSerializable("companyDetail");
        }

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderProfile = findViewById(R.id.ivHeaderProfile);

        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        LinearLayout lyArtistDetail = findViewById(R.id.lyArtistDetail);
        tvHeaderTitle.setText(getString(R.string.company));
        tvUserName = findViewById(R.id.tvUserName);
        TextView tvServices = findViewById(R.id.tvServices);
        AppCompatButton btnEditWhs = findViewById(R.id.btnEditWhs);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSocialMedia = findViewById(R.id.tvSocialMedia);
        tvHoliday = findViewById(R.id.tvHoliday);

        //   apiForGetStaffDetail();

        setView(companyDetail);

        ivHeaderBack.setOnClickListener(this);
        tvSocialMedia.setOnClickListener(this);
        tvServices.setOnClickListener(this);
        btnEditWhs.setOnClickListener(this);
        tvJobTitle.setOnClickListener(this);

    }

    private void setView(CompanyDetail staffDetail){
        if (staffDetail!=null) {
            tvUserName.setText(staffDetail.businessName);

            if (!staffDetail.profileImage.isEmpty())
                Picasso.with(CompanyDetailActivity.this).load(staffDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                        fit().into(ivHeaderProfile);
            else
                ivHeaderProfile.setImageDrawable(getResources().getDrawable(R.drawable.defoult_user_img));

            if (staffDetail.job != null){
                tvJobTitle.setText(staffDetail.job);
                tvSocialMedia.setText(staffDetail.mediaAccess);
                if (!staffDetail.holiday.equals(""))
                    tvHoliday.setText(staffDetail.holiday);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeaderBack:
                finish();
                break;

            case R.id.tvServices:
                Intent intent = new Intent(CompanyDetailActivity.this,CompanyServicesActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("companyDetail", companyDetail);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
                break;

            case R.id.btnEditWhs:
                if (companyDetail != null) {
                    companyDetail.businessDays.clear();
                    HashMap<Integer,BusinessDay> hashMap = new HashMap<>();
                    ArrayList<TimeSlot> timeSlots = new ArrayList<>();
                    for (int i=0; i<companyDetail.staffHoursList.size(); i++){
                        BusinessDay day = new BusinessDay();
                        BusinessDayForStaff dayForStaff = companyDetail.staffHoursList.get(i);
                        day.dayId = dayForStaff.day;

                        switch (day.dayId){
                            case 0:
                                day.isOpen = true;
                                day.dayName = getString(R.string.monday);
                                break;
                            case 1:
                                day.isOpen = true;
                                day.dayName = getString(R.string.tuesday);
                                break;
                            case 2:
                                day.isOpen = true;
                                day.dayName = getString(R.string.wednesday);
                                break;
                            case 3:
                                day.isOpen = true;
                                day.dayName = getString(R.string.thursday);
                                break;
                            case 4:
                                day.isOpen = true;
                                day.dayName = getString(R.string.frieday);
                                break;
                            case 5:
                                day.isOpen = true;
                                day.dayName = getString(R.string.saturday);
                                break;
                            case 6:
                                day.isOpen = true;
                                day.dayName = getString(R.string.sunday);
                                break;
                        }

                        TimeSlot timeSlotNew = new TimeSlot(day.dayId);
                        timeSlotNew.startTime = dayForStaff.startTime;
                        timeSlotNew.endTime = dayForStaff.endTime;
                        timeSlotNew.dayId = day.dayId;

                        if (timeSlots.size()!=0){
                            if (timeSlots.get(0).dayId == day.dayId){
                                timeSlots.add(1,timeSlotNew);
                            }else {
                                timeSlots.clear();
                                timeSlots.add(timeSlotNew);
                            }
                        }else {
                            timeSlots.add(timeSlotNew);
                        }

                        day.slots.addAll(timeSlots);

                        hashMap.put(day.dayId,day);
                    }
                    if (hashMap.size()!=0){

                        for (Object o : hashMap.entrySet()) {
                            Map.Entry pair = (Map.Entry) o;
                            BusinessDay businessDay = hashMap.get(pair.getKey());
                            companyDetail.businessDays.add(businessDay);
                            //    it.remove();
                        }
                        Collections.sort(companyDetail.businessDays, new Comparator<BusinessDay>() {
                            @Override
                            public int compare(BusinessDay a, BusinessDay b)
                            {
                                return a.dayId > b.dayId ? +1 : a.dayId < b.dayId ? -1 : 0;
                            }
                        });
                    }

                    showDialog(companyDetail.businessDays);
                }
                break;
        }
    }

    public  void showDialog(List<BusinessDay>businessDays) {
        View DialogView = View.inflate(CompanyDetailActivity.this, R.layout.dialog_business_hours, null);

        final Dialog alertDailog = new Dialog(CompanyDetailActivity.this, android.R.style.Theme_Light);
        alertDailog.setCanceledOnTouchOutside(true);
        alertDailog.setCancelable(true);

        alertDailog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDailog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDailog.getWindow().getAttributes().windowAnimations = R.style.InOutAnimation;
        alertDailog.setContentView(DialogView);

        AdapCompanyBusinessDays adapter = new AdapCompanyBusinessDays(CompanyDetailActivity.this, businessDays);

        ImageView ivHeaderBack = DialogView.findViewById(R.id.ivHeaderBack2);

        RecyclerView rvBusinessDay = DialogView.findViewById(R.id.rvBusinessDay);
        rvBusinessDay.setLayoutManager(new LinearLayoutManager(CompanyDetailActivity.this));
        rvBusinessDay.setAdapter(adapter);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDailog.cancel();
            }
        });

        alertDailog.show();
    }

}
