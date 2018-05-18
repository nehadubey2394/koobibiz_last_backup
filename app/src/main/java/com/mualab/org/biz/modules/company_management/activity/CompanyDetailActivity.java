package com.mualab.org.biz.modules.company_management.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.booking.Company;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.util.StatusBarUtil;
import com.squareup.picasso.Picasso;

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
            tvUserName.setText(staffDetail.userName);

            Picasso.with(CompanyDetailActivity.this).load(staffDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(ivHeaderProfile);

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
        }
    }
}
