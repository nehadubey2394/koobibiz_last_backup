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
import com.mualab.org.biz.module.add_staff.cv_popup.CustomPopupWindow;
import com.mualab.org.biz.module.add_staff.listner.PoppupWithListListner;

import java.util.ArrayList;

public class AddStaffDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvJobTitle,tvSocialMedia,tvHoliday;

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
     /*   if (intent!=null){
            bookingId =  intent.getStringExtra("bookingId");
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffList = (ArrayList<Staff>) args.getSerializable("ARRAYLIST");
        }else {
            staffList = new ArrayList<>();
        }*/

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ImageView ivHeaderProfile = findViewById(R.id.ivHeaderProfile);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_staff));
        TextView tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText("Staff");

        TextView tvServices = findViewById(R.id.tvServices);
        AppCompatButton btnEditWhs = findViewById(R.id.btnEditWhs);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSocialMedia = findViewById(R.id.tvSocialMedia);
        tvHoliday = findViewById(R.id.tvHoliday);

        ivHeaderBack.setOnClickListener(this);
        tvHoliday.setOnClickListener(this);
        tvSocialMedia.setOnClickListener(this);
        tvServices.setOnClickListener(this);
        btnEditWhs.setOnClickListener(this);
        tvJobTitle.setOnClickListener(this);
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
                arrayList.add("Expert");
                arrayList.add("Intermediate");
                arrayList.add("Bignner");

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
                socialMediaList.add("Facebook");
                socialMediaList.add("Twitter");
                socialMediaList.add("Instagram");

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
