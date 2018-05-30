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
import android.widget.LinearLayout;
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
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.serializer.TimeSlotSerializer;
import com.mualab.org.biz.modules.add_staff.fragments.ArtistLastServicesFragment;
import com.mualab.org.biz.modules.add_staff.fragments.EditBusinessHoursFragment;
import com.mualab.org.biz.modules.add_staff.listner.EditWorkingHours;
import com.mualab.org.biz.modules.profile.fragment.FragmentListner;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.StatusBarUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStaffDetailActivity extends AppCompatActivity implements View.OnClickListener,
        FragmentListner,EditWorkingHours {
    private TextView tvJobTitle,tvSocialMedia,tvHoliday;
    private StaffDetail staffDetail;
    private ArrayList<String> sIds;
    private AppCompatButton btnSave;
    private boolean isChangeOccured = false;
    private String whJsonArray;
    private TextView tvHeaderTitle,tvUserName;
    private LinearLayout lyArtistDetail;
    private PreRegistrationSession pSession;
    private List<TimeSlot> slotList;
    private ImageView ivHeaderProfile;

    public void setHeaderVisibility(int visibility){
        if(lyArtistDetail!=null)
            lyArtistDetail.setVisibility(visibility);
    }

    public void setTitle(String text){
        if(tvHeaderTitle!=null)
            tvHeaderTitle.setText(text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_detail);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        initView();
    }

    private void initView(){
        sIds = new ArrayList<>();
        slotList = new ArrayList<>();
        // staffDetail = new StaffDetail();

        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffDetail = (StaffDetail) args.getSerializable("staff");
            //  staffId = args.getString("staffId");
            //  isEdit = args.getBoolean("isEdit");
            //  staffDetail.staffId = staffId;
        }

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);

        ivHeaderProfile = findViewById(R.id.ivHeaderProfile);

        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        lyArtistDetail = findViewById(R.id.lyArtistDetail);
        tvHeaderTitle.setText(getString(R.string.text_staff));
        tvUserName = findViewById(R.id.tvUserName);
        btnSave = findViewById(R.id.btnSave);
        TextView tvServices = findViewById(R.id.tvServices);
        AppCompatButton btnEditWhs = findViewById(R.id.btnEditWhs);
        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSocialMedia = findViewById(R.id.tvSocialMedia);
        tvHoliday = findViewById(R.id.tvHoliday);

        pSession = Mualab.getInstance().getBusinessProfileSession();

        //   apiForGetStaffDetail();

        setView(staffDetail);

        for(int i = 0;i<staffDetail.staffServices.size();i++) {
            sIds.add(staffDetail.staffServices.get(i).artistServiceId);
        }
        List<BusinessDayForStaff> businessDays = pSession.getBusinessProfile().dayForStaffs;
        Gson gson = new GsonBuilder().registerTypeAdapter(TimeSlot.class, new TimeSlotSerializer()).create();
        if ((staffDetail != null ? staffDetail.staffHoursList.size() : 0) !=0){
            whJsonArray = gson.toJson(staffDetail.staffHoursList);
        }else {
            whJsonArray = gson.toJson(businessDays);
        }

        ivHeaderBack.setOnClickListener(this);
        tvSocialMedia.setOnClickListener(this);
        tvServices.setOnClickListener(this);
        btnEditWhs.setOnClickListener(this);
        tvJobTitle.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private void setView(StaffDetail staffDetail){
        if (staffDetail!=null) {
            tvUserName.setText(staffDetail.userName);

            if (!staffDetail.profileImage.equals("")){
                Picasso.with(AddStaffDetailActivity.this).load(staffDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                        fit().into(ivHeaderProfile);
            }

            if (staffDetail.job != null){
                tvJobTitle.setText(staffDetail.job);
                tvSocialMedia.setText(staffDetail.mediaAccess);
                if (!staffDetail.holiday.equals(""))
                    tvHoliday.setText(staffDetail.holiday);
            }

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
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSave:
                // JsonArray whJsonArray = gson.toJsonTree(staffDetail.staffHoursList).getAsJsonArray();
                String jobTltle = tvJobTitle.getText().toString().trim();
                String mediaAccess =  tvSocialMedia.getText().toString().trim();
                String holiday =  tvHoliday.getText().toString().trim();

                if (!jobTltle.equals(""))
                {
                    if (sIds.size()!=0){
                        if (!mediaAccess.equals("")) {

                            if (!whJsonArray.isEmpty())
                                apiForAddStaff(jobTltle, mediaAccess, holiday);
                            else
                                MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select working hours for staff");
                        }
                        else
                            MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select Social Media Access");
                    }else
                        MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select at least one service for staff");
                } else
                    MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select Job Title");

                break;

            case R.id.ivHeaderBack:
                onBackPressed();
                break;
            case R.id.tvJobTitle:
                showJobTile();

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
                staffDetail.businessDays.clear();
                HashMap<Integer,BusinessDay> hashMap = new HashMap<>();
                List<BusinessDay> days = pSession.getBusinessProfile().businessDays;

                if (slotList.size()!=0){
                    for(BusinessDay tmpDay : days){
                        int slotIndex = 0;
                        BusinessDay businessDay = new BusinessDay();
                        for (TimeSlot dayForStaff : slotList){
                            if(tmpDay.dayId == dayForStaff.dayId){
                                if (businessDay.getTimeSlotSize()>1)
                                    slotIndex++;
                                businessDay.dayName = tmpDay.dayName;
                                businessDay.dayId = tmpDay.dayId;
                                TimeSlot slot = new TimeSlot(tmpDay.dayId);
                                slot.id = tmpDay.id;
                                slot.startTime = tmpDay.slots.get(slotIndex).startTime;
                                slot.endTime = tmpDay.slots.get(slotIndex).endTime;
                                slot.edtStartTime = dayForStaff.startTime;
                                slot.edtEndTime = dayForStaff.endTime;
                                slot.status = 1;
                                slot.slotTime =  slot.startTime+"-"+dayForStaff.endTime;
                                businessDay.isOpen = true;
                                businessDay.addTimeSlot(slot);
                                hashMap.put(tmpDay.dayId,businessDay);
                            }
                        }
                    }
                    if (hashMap.size()!=0){

                        for (Object o : hashMap.entrySet()) {
                            Map.Entry pair = (Map.Entry) o;
                            BusinessDay businessDay = hashMap.get(pair.getKey());
                            staffDetail.businessDays.add(businessDay);
                            //    it.remove();
                        }
                        Collections.sort(staffDetail.businessDays, new Comparator<BusinessDay>() {
                            @Override
                            public int compare(BusinessDay a, BusinessDay b)
                            {
                                return a.dayId > b.dayId ? +1 : a.dayId < b.dayId ? -1 : 0;
                            }
                        });
                    }

                }else {
                    if ((staffDetail != null ? staffDetail.staffHoursList.size() : 0) !=0) {

                        for (BusinessDay tmpDay : days) {
                            int slotIndex = 0;
                            BusinessDay businessDay = new BusinessDay();
                            for (BusinessDayForStaff dayForStaff : staffDetail.staffHoursList) {
                                if (tmpDay.dayId == dayForStaff.day) {
                                    if (businessDay.getTimeSlotSize() > 1)
                                        slotIndex++;
                                    businessDay.dayName = tmpDay.dayName;
                                    businessDay.dayId = tmpDay.dayId;
                                    TimeSlot slot = new TimeSlot(tmpDay.dayId);
                                    slot.id = tmpDay.id;
                                    slot.startTime = tmpDay.slots.get(slotIndex).startTime;
                                    slot.endTime = tmpDay.slots.get(slotIndex).endTime;
                                    slot.edtStartTime = dayForStaff.startTime;
                                    slot.edtEndTime = dayForStaff.endTime;
                                    slot.status = 1;
                                    slot.slotTime = slot.startTime + "-" + dayForStaff.endTime;
                                    businessDay.isOpen = true;
                                    businessDay.addTimeSlot(slot);
                                    hashMap.put(tmpDay.dayId, businessDay);
                                }
                            }
                        }

                        if (hashMap.size() != 0) {

                            for (Object o : hashMap.entrySet()) {
                                Map.Entry pair = (Map.Entry) o;
                                BusinessDay businessDay = hashMap.get(pair.getKey());
                                staffDetail.businessDays.add(businessDay);
                                //    it.remove();
                            }
                            Collections.sort(staffDetail.businessDays, new Comparator<BusinessDay>() {
                                @Override
                                public int compare(BusinessDay a, BusinessDay b) {
                                    return a.dayId > b.dayId ? +1 : a.dayId < b.dayId ? -1 : 0;
                                }
                            });
                        }
                    }
                }
                pSession.setStaffBusinessHours(staffDetail);
                addFragment( new EditBusinessHoursFragment(),true,R.id.rlContainer);
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

    private void apiForAddStaff(final String jobTltle, final String mediaAccess,final String holiday){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddStaffDetailActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForAddStaff(jobTltle,mediaAccess,holiday);
                    }
                }
            }).show();
        }

        Gson gson2 = new GsonBuilder().create();
        JsonArray serviceArray = gson2.toJsonTree(sIds).getAsJsonArray();


        Map<String, String> params = new HashMap<>();
        params.put("artistId", staffDetail.staffId);
        params.put("staffId", staffDetail.staffId);
        params.put("businessId", String.valueOf(user.id));
        params.put("staffService", String.valueOf(serviceArray));
        params.put("job",jobTltle);
        params.put("holiday",holiday);
        params.put("serviceType",user.serviceType);
        params.put("mediaAccess",mediaAccess );
        params.put("staffHours", whJsonArray);

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
                        Intent intent = new Intent(AddStaffDetailActivity.this, AddStaffActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
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
                sIds = data.getStringArrayListExtra("jsonArray");
                staffDetail = (StaffDetail) data.getSerializableExtra("staffDetail");
            }
        }
    }

    private void showAlertDailog(){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(AddStaffDetailActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure want to discard changes?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                //  ArtistLastServicesFragment.selectedServicesList.clear();
                ArtistLastServicesFragment.localMap.clear();
                dialog.cancel();
                finish();
                //    startActivity(new Intent(AddStaffDetailActivity.this,AddStaffActivity.class));

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
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();

        if (i > 0) {
            fm.popBackStack();
        }else {

            if (ArtistLastServicesFragment.localMap.size() > 0 || isChangeOccured)
                showAlertDailog();
            else {
                finish();
                //   startActivity(new Intent(AddStaffDetailActivity.this, AddStaffActivity.class));
            }
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

    @Override
    public void onHorusChange(String JsonArray,List<TimeSlot> slotList) {
        isChangeOccured = true;
        this.whJsonArray = JsonArray;
        this.slotList = slotList;
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }
}
