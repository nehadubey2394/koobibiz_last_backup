package com.mualab.org.biz.modules.add_staff.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AllArtist;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.add_staff.cv_popup.CustomPopupWindow;
import com.mualab.org.biz.modules.add_staff.listner.PoppupWithListListner;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        AppCompatButton btnSave = findViewById(R.id.btnSave);

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
        btnSave.setOnClickListener(this);
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
            case R.id.btnSave:
                PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
                List<BusinessDay> businessDays = pSession.getBusinessProfile().businessDays;
                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(businessDays).getAsJsonArray();
                apiForAddStaff(jsonArray);
                break;
            case R.id.ivHeaderBack:
                onBackPressed();
                break;
            case R.id.tvJobTitle:
                showJobTile();
            /*    final ArrayList<String>arrayList = new ArrayList<>();
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
                }*/

                break;
            case R.id.tvSocialMedia:
                showMediaAccess();
              /*  final ArrayList<String>socialMediaList = new ArrayList<>();
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
                }*/
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

    public void showMediaAccess() {
        {
            final CharSequence[] items = {getString(R.string.admin), getString(R.string.editor),
                    getString(R.string.moderator)};
            AlertDialog.Builder alert = new AlertDialog.Builder(AddStaffDetailActivity.this);
            alert.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals(getString(R.string.admin))) {
                        tvSocialMedia.setText(getString(R.string.admin));
                        dialog.cancel();
                    } else if (items[item].equals(getString(R.string.editor))) {
                        tvSocialMedia.setText(getString(R.string.editor));
                        dialog.cancel();
                    } else if (items[item].equals(getString(R.string.moderator))) {
                        tvSocialMedia.setText(getString(R.string.moderator));
                        dialog.cancel();
                    }
                }
            });
            alert.show();
        }
    }

    public void showJobTile() {
        {
            final CharSequence[] items = {getString(R.string.beginner), getString(R.string.moderate),
                    getString(R.string.expert)};
            AlertDialog.Builder alert = new AlertDialog.Builder(AddStaffDetailActivity.this);
            alert.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals(getString(R.string.beginner))) {
                        tvJobTitle.setText(getString(R.string.beginner));
                        dialog.cancel();
                    } else if (items[item].equals(getString(R.string.moderate))) {
                        tvJobTitle.setText(getString(R.string.moderate));
                        dialog.cancel();
                    } else if (items[item].equals(getString(R.string.expert))) {
                        tvJobTitle.setText(getString(R.string.expert));
                        dialog.cancel();
                    }
                }
            });
            alert.show();
        }
    }

    private void apiForAddStaff(final JsonArray jsonArray){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddStaffDetailActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForAddStaff(jsonArray);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staffDetail.staffId);
        params.put("staffId", staffDetail.staffId);
        params.put("businessId", String.valueOf(user.id));
        params.put("staffService", "");
        params.put("job", tvJobTitle.getText().toString().trim());
        params.put("mediaAccess", tvSocialMedia.getText().toString().trim());
        params.put("staffHours", jsonArray.toString());
        params.put("type", "");

        HttpTask task = new HttpTask(new HttpTask.Builder(AddStaffDetailActivity.this, "addStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                } catch (Exception e) {
                    Progress.hide(AddStaffDetailActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
