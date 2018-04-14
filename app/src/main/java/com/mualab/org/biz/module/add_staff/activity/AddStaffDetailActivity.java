package com.mualab.org.biz.module.add_staff.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.add_staff.AllArtist;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.module.add_staff.cv_popup.CustomPopupWindow;
import com.mualab.org.biz.module.add_staff.listner.PoppupWithListListner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddStaffDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvJobTitle,tvSocialMedia,tvHoliday;
    private StaffDetail staffDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_detail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffDetail = (StaffDetail) args.getSerializable("staff");
        }

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ImageView ivHeaderProfile = findViewById(R.id.ivHeaderProfile);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_staff));
        TextView tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(staffDetail.userName);

        if (!staffDetail.profileImage.equals("")){
            Picasso.with(AddStaffDetailActivity.this).load(staffDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(ivHeaderProfile);
        }

        TextView tvServices = findViewById(R.id.tvServices);
        AppCompatButton btnEditWhs = findViewById(R.id.btnEditWhs);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSocialMedia = findViewById(R.id.tvSocialMedia);
        tvHoliday = findViewById(R.id.tvHoliday);

        setView();

        ivHeaderBack.setOnClickListener(this);
        tvHoliday.setOnClickListener(this);
        tvSocialMedia.setOnClickListener(this);
        tvServices.setOnClickListener(this);
        btnEditWhs.setOnClickListener(this);
        tvJobTitle.setOnClickListener(this);
    }

    private void setView(){
        if (staffDetail.job != null){
            tvJobTitle.setText(staffDetail.job);
            tvSocialMedia.setText(staffDetail.mediaAccess);
            if (!staffDetail.holiday.equals(""))
                tvHoliday.setText(staffDetail.holiday);
        }
    }
    @Override
    public void onClick(View view) {
        CustomPopupWindow dialogsClass = new CustomPopupWindow();

        switch (view.getId()) {
            case R.id.ivHeaderBack:
                onBackPressed();
                break;
            case R.id.tvJobTitle:
                final ArrayList<String>arrayList = new ArrayList<>();
                arrayList.add("Beginner");
                arrayList.add("Moderate");
                arrayList.add("Expert");

                if (arrayList.size() > 0) {
                    //   showFilterDialog(years);
                    dialogsClass.poppup_window_with_list(this, new PoppupWithListListner() {
                        @Override
                        public void selectedoption(int id) {
                            tvJobTitle.setText(arrayList.get(id));
                        }
                    }, tvJobTitle, arrayList);
                }

                break;
            case R.id.tvSocialMedia:
                final ArrayList<String>socialMediaList = new ArrayList<>();
                socialMediaList.add("Admin");
                socialMediaList.add("Editor");
                socialMediaList.add("Moderator");

                if (socialMediaList.size() > 0) {
                    //   showFilterDialog(years);
                    dialogsClass.poppup_window_with_list(this, new PoppupWithListListner() {
                        @Override
                        public void selectedoption(int id) {
                            tvSocialMedia.setText(socialMediaList.get(id));
                        }
                    }, tvSocialMedia, socialMediaList);
                }
                break;

            case R.id.tvServices:
                Intent intent = new Intent(AddStaffDetailActivity.this,AllServicesActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("staffDetail", staffDetail);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
                break;

            case R.id.btnEditWhs:

                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
