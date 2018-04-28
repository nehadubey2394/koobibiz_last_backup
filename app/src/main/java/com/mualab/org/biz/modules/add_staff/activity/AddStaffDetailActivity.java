package com.mualab.org.biz.modules.add_staff.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
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
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AddedStaffServices;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.add_staff.fragments.ArtistLastServicesFragment;
import com.mualab.org.biz.modules.add_staff.fragments.EditBusinessHoursFragment;
import com.mualab.org.biz.modules.profile.fragment.FragmentListner;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStaffDetailActivity extends AppCompatActivity implements View.OnClickListener,FragmentListner {
    private TextView tvJobTitle,tvSocialMedia,tvHoliday;
    public StaffDetail staffDetail;
    private String[] sIds;
    private AppCompatButton btnSave;
    private boolean isChangeOccured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_detail);
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


        btnSave = findViewById(R.id.btnSave);

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

        tvHoliday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f);
                    isChangeOccured = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivHeaderBack.setOnClickListener(this);
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

        sIds = new String[staffDetail.staffServices.size()];

        for(int i = 0;i<staffDetail.staffServices.size();i++) {
            sIds[i] = staffDetail.staffServices.get(i).artistServiceId;
        }
    }

    public StaffDetail getStaffDetail(){
        return staffDetail;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSave:
                PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
                List<BusinessDayForStaff> businessDays = pSession.getBusinessProfile().dayForStaffs;
                Gson gson = new GsonBuilder().create();
                JsonArray whJsonArray = gson.toJsonTree(businessDays).getAsJsonArray();
                String jobTltle = tvJobTitle.getText().toString().trim();
                String mediaAccess =  tvSocialMedia.getText().toString().trim();
                String holiday =  tvHoliday.getText().toString().trim();

                if (sIds!=null){
                    if (!jobTltle.equals(""))
                    {
                        if (!mediaAccess.equals(""))
                            apiForAddStaff(whJsonArray,jobTltle,mediaAccess,holiday);
                        else
                            MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select Social Media Access");
                    }
                    else
                        MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select Job Title");
                }else
                    MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select service");

                break;

            case R.id.ivHeaderBack:
                onBackPressed();
                break;
            case R.id.tvJobTitle:
                showJobTile();
                // CustomPopupWindow dialogsClass = new CustomPopupWindow();

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
                break;

            case R.id.tvServices:
                Intent intent = new Intent(AddStaffDetailActivity.this,AllServicesActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("staffDetail", staffDetail);
                intent.putExtra("BUNDLE", args);
                startActivityForResult(intent,10);
                break;

            case R.id.btnEditWhs:
                addFragment( EditBusinessHoursFragment.newInstance(),true,R.id.rlContainer);
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
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f);
                    isChangeOccured = true;
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
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f);
                    isChangeOccured = true;
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

    private void apiForAddStaff(final JsonArray whJsonArray,final String jobTltle,
                                final String mediaAccess,final String holiday){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddStaffDetailActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForAddStaff(whJsonArray,jobTltle,mediaAccess,holiday);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staffDetail.staffId);
        // params.put("staffId", staffDetail.staffId);
        params.put("businessId", String.valueOf(user.id));
        params.put("staffService", Arrays.toString(sIds));
        params.put("job",jobTltle);
        params.put("holiday",holiday);
        params.put("serviceType","");
        params.put("mediaAccess",mediaAccess );
        params.put("staffHours", whJsonArray.toString());

        if (staffDetail.job != null && staffDetail.staffServices.size()!=0) {
            params.put("type", "edit");
            params.put("editId", staffDetail._id);
        }
        else {
            params.put("type", "");
            params.put("editId", "");
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AddStaffDetailActivity.this, "addStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        //ArtistLastServicesFragment.selectedServicesList.clear();
                        ArtistLastServicesFragment.localMap.clear();
                        MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Staff added successfully");
                        finish();
                        startActivity(new Intent(AddStaffDetailActivity.this,AddStaffActivity.class));

                    }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10 && resultCode!=0){
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
            isChangeOccured = true;
            if (data!=null){
                sIds = data.getStringArrayExtra("jsonArray");
            }
        }
    }

    private void showAlertDailog(){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(AddStaffDetailActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure you want to permanently remove all selected services?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                //  ArtistLastServicesFragment.selectedServicesList.clear();
                ArtistLastServicesFragment.localMap.clear();
                dialog.cancel();
                finish();
                startActivity(new Intent(AddStaffDetailActivity.this,AddStaffActivity.class));

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    public void addFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_in,0,0);
            transaction.add(containerId, fragment, backStackName);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }

    }

    @Override
    public void onBackPressed() {
        if (ArtistLastServicesFragment.localMap.size()>0 || isChangeOccured)
            showAlertDailog();
        else {
            finish();
            startActivity(new Intent(AddStaffDetailActivity.this,AddStaffActivity.class));
        }
    }

    @Override
    public void onNext() {

    }

    @Override
    public void onPrev() {

    }

    @Override
    public void onChangeByTag(String Tag) {

    }

    @Override
    public void onFinish() {

    }
}
