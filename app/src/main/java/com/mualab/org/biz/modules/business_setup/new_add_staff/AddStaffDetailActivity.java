package com.mualab.org.biz.modules.business_setup.new_add_staff;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.serializer.TimeSlotSerializer;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStaffDetailActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView tvJobTitle,tvSocialMedia;
    private StaffDetail staffDetail;
    private ArrayList<String> sIds;
    private AppCompatButton btnContinue;
    private boolean isChangeOccured = false;
    private String whJsonArray;
    private TextView tvUserName;
    private List<BusinessDay> edtTimeSlotList;
    private ImageView ivHeaderProfile;
    private RelativeLayout rlJobTitle,rlServices,rlMediaAccess,rlWorkingHours;
    private Spinner spJobTitle,spMediaAccess;
    private EditText etHoliday;
    private Boolean isSlotExist = false;

    public void setHeaderVisibility(int visibility){
    }

    public void setTitle(String text){
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff_detail);
        //  StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));
        initView();
    }

    private void initView(){
        sIds = new ArrayList<>();
        edtTimeSlotList = new ArrayList<>();

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_staff));
        // staffDetail = new StaffDetail();

        Intent intent = getIntent();
        if (intent!=null){
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffDetail = (StaffDetail) args.getSerializable("staff");
            //  staffId = args.getString("staffId");
            //  isEdit = args.getBoolean("isEdit");
            //  staffDetail.staffId = staffId;
        }

        ivHeaderProfile = findViewById(R.id.ivHeaderProfile);

        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.add_staff));

        tvUserName = findViewById(R.id.tvUserName);
        spJobTitle = findViewById(R.id.spJobTitle);
        spMediaAccess = findViewById(R.id.spMediaAccess);
        btnContinue = findViewById(R.id.btnContinue);

        rlJobTitle = findViewById(R.id.rlJobTitle);
        rlServices = findViewById(R.id.rlServices);
        rlMediaAccess = findViewById(R.id.rlMediaAccess);
        rlWorkingHours = findViewById(R.id.rlWorkingHours);

        tvJobTitle = findViewById(R.id.tvJobTitle);
        tvSocialMedia = findViewById(R.id.tvSocialMedia);
        etHoliday = findViewById(R.id.etHoliday);
        etHoliday = findViewById(R.id.etHoliday);

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();

        //   apiForGetStaffDetail();

        setView(staffDetail);

        for(int i = 0;i<staffDetail.staffServices.size();i++) {
            sIds.add(staffDetail.staffServices.get(i).artistServiceId);
        }

        List<BusinessDayForStaff> businessDays = pSession.getBusinessProfile().dayForStaffs;
        //List<BusinessDayForStaff> businessDays = pSession.getBusinessProfile().dayForStaffs;


        Gson gson = new GsonBuilder().registerTypeAdapter(TimeSlot.class,
                new TimeSlotSerializer()).create();

        if ((staffDetail != null ? staffDetail.staffHoursList.size() : 0) !=0){
            whJsonArray = gson.toJson(staffDetail.staffHoursList);
            BusinessProfile businessProfile =  pSession.getBusinessProfile();

            for (BusinessDay companyTime : businessProfile.businessDays){

                BusinessDay newTime = new BusinessDay();
                newTime.dayName = companyTime.dayName;
                newTime.dayId = companyTime.dayId;
                isSlotExist = false;

                for (BusinessDayForStaff day : staffDetail.staffHoursList){

                    if (day.day==companyTime.dayId){

                        TimeSlot slot = new TimeSlot(companyTime.dayId);
                        slot.startTime = day.startTime;
                        slot.endTime = day.endTime;

                        if (companyTime.slots.size()==1) {
                            slot.minStartTime = companyTime.slots.get(0).minStartTime;
                            slot.maxEndTime = companyTime.slots.get(0).maxEndTime;
                        }else if (companyTime.slots.size()>1){
                            if (isSlotExist){
                                slot.minStartTime = companyTime.slots.get(1).minStartTime;
                                slot.maxEndTime = companyTime.slots.get(1).maxEndTime;
                            }else{
                                slot.minStartTime = companyTime.slots.get(0).minStartTime;
                                slot.maxEndTime = companyTime.slots.get(0).maxEndTime;
                            }
                        }

                        slot.edtStartTime = day.startTime;
                        slot.edtEndTime = day.endTime;
                        newTime.isExpand = true;
                        newTime.isOpen = true;

                        newTime.addTimeSlot(slot);
                        if (isSlotExist)
                            break;

                        isSlotExist = true;
                    }

                }
                if (companyTime.isOpen)
                    edtTimeSlotList.add(newTime);
            }

        }else {
            whJsonArray = gson.toJson(businessDays);
        }

        ivHeaderBack.setOnClickListener(this);
        tvSocialMedia.setOnClickListener(this);
        tvJobTitle.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        rlWorkingHours.setOnClickListener(this);
        rlServices.setOnClickListener(this);
    }

    private void setView(StaffDetail staffDetail){
        if (staffDetail!=null) {
            tvUserName.setText(staffDetail.userName);

            if (!staffDetail.profileImage.equals("")){
                Picasso.with(AddStaffDetailActivity.this).load(staffDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                        fit().into(ivHeaderProfile);
            }

            final ArrayList<String>arrayList = new ArrayList<>();



            if (staffDetail.job!=null && !staffDetail.job.equals("")) {
                //tvJobTitle.setText(staffDetail.job);
                if (staffDetail.job.equalsIgnoreCase(getString(R.string.beginner))) {
                    arrayList.add(getString(R.string.beginner));
                    arrayList.add(getString(R.string.moderate));
                    arrayList.add(getString(R.string.expert));
                }else if (staffDetail.job.equalsIgnoreCase(getString(R.string.moderate))) {
                    arrayList.add(getString(R.string.moderate));
                    arrayList.add(getString(R.string.beginner));
                    arrayList.add(getString(R.string.expert));
                }else if (staffDetail.job.equalsIgnoreCase(getString(R.string.expert))) {
                    arrayList.add(getString(R.string.expert));
                    arrayList.add(getString(R.string.beginner));
                    arrayList.add(getString(R.string.moderate));

                }

            }else {
                arrayList.add(getString(R.string.beginner));
                arrayList.add(getString(R.string.moderate));
                arrayList.add(getString(R.string.expert));
            }

            ArrayAdapter arrayAdapter = new ArrayAdapter(AddStaffDetailActivity.this
                    ,R.layout.layout_spinner_items, arrayList);

            spJobTitle.setAdapter(arrayAdapter);
            spJobTitle.setPrompt("");
            spJobTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spJobTitle.setPrompt("");
                    TextView textView =  view.findViewById(R.id.tvSpItem);
                    textView.setText("");
                    tvJobTitle.setText(arrayList.get(i));
                    tvJobTitle.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // textv1.setText(getString(R.string.business_type));
                    // tvBizType.setVisibility(View.GONE);
                }
            });


            final ArrayList<String>mediaAccessList = new ArrayList<>();
            mediaAccessList.add(getString(R.string.admin));
            mediaAccessList.add(getString(R.string.editor));
            mediaAccessList.add(getString(R.string.moderator));

            ArrayAdapter arrayAdapter2 = new ArrayAdapter(AddStaffDetailActivity.this
                    ,R.layout.layout_spinner_items, mediaAccessList);

            spMediaAccess.setAdapter(arrayAdapter2);
            spMediaAccess.setPrompt("");
            spMediaAccess.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    spMediaAccess.setPrompt("");
                    TextView textView =  view.findViewById(R.id.tvSpItem);
                    textView.setText("");
                    tvSocialMedia.setText(mediaAccessList.get(i));
                    tvSocialMedia.setVisibility(View.VISIBLE);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    // textv1.setText(getString(R.string.business_type));
                    // tvBizType.setVisibility(View.GONE);
                }
            });


            etHoliday.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.length()>0){
                        btnContinue.setEnabled(true);
                        btnContinue.setAlpha(1.0f);
                        isChangeOccured = true;
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
        }

        assert staffDetail != null;
        if (staffDetail.job != null){
            tvJobTitle.setText(staffDetail.job);
            tvSocialMedia.setText(staffDetail.mediaAccess);
            if (!staffDetail.holiday.equals(""))
                etHoliday.setText(staffDetail.holiday);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnContinue:

                // JsonArray whJsonArray = gson.toJsonTree(staffDetail.staffHoursList).getAsJsonArray();
                String jobTltle = tvJobTitle.getText().toString().trim();
                String mediaAccess =  tvSocialMedia.getText().toString().trim();
                String holiday =  etHoliday.getText().toString().trim();

                if (!jobTltle.equals(""))
                {
                    if (sIds.size()!=0){
                        // if (!mediaAccess.equals("")) {
                        // if (!holiday.equals("")){
                        if (!whJsonArray.isEmpty())
                            // MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select working hours for staff");
                            apiForAddStaff(jobTltle, mediaAccess, holiday);
                        else
                            MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select working hours for staff");

                        //}else
                        //    MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Please enter holiday allocation");
                        //   } else MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select Social Media Access");

                    }else
                        MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select at least one service for staff");
                } else
                    MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Select Job Title");

                break;

            case R.id.ivHeaderBack:
                onBackPressed();
                break;

            case R.id.rlWorkingHours:
                Intent intent2 = new Intent(AddStaffDetailActivity.this,
                        StaffWorkingHoursActivity.class);
                if (edtTimeSlotList.size()!=0){
                    Bundle args = new Bundle();
                    args.putSerializable("edtTimeSlotList",(Serializable)edtTimeSlotList);
                    intent2.putExtra("BUNDLE",args);
                    startActivityForResult(intent2,11);
                }else
                    startActivityForResult(intent2,11);

                break;

            case R.id.rlServices:
                //  Intent intent = new Intent(AddStaffDetailActivity.this,AllServicesActivity.class);
                Intent intent = new Intent(AddStaffDetailActivity.this,AddStaffServicesActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("staffDetail", staffDetail);
                intent.putExtra("BUNDLE", args);
                startActivityForResult(intent,10);
                break;

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
                        AddStaffServicesActivity.localServiceMap.clear();
                        // MyToast.getInstance(AddStaffDetailActivity.this).showDasuAlert("Staff added successfully");
                        //  Intent intent = new Intent(AddStaffDetailActivity.this, AddNewStaffActivity.class);
                        //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        Intent intent = new Intent();
                        // intent.putExtra("jsonArray", sIds);
                        setResult(RESULT_OK, intent);
                        finish();
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
            btnContinue.setEnabled(true);
            btnContinue.setAlpha(1.0f);
            isChangeOccured = true;
            if (data!=null){
                sIds = data.getStringArrayListExtra("jsonArray");
                staffDetail = (StaffDetail) data.getSerializableExtra("staffDetail");
            }
        }if (requestCode==11 && resultCode!=0){
            edtTimeSlotList.clear();

            Bundle args = data.getBundleExtra("BUNDLE");
            ArrayList<BusinessDay> arraylist = (ArrayList<BusinessDay>) args.getSerializable("ARRAYLIST");

            String JsonArray = args.getString("jsonArray");

            isChangeOccured = true;
            this.whJsonArray = JsonArray;
            edtTimeSlotList = arraylist;
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
                AddStaffServicesActivity.localServiceMap.clear();
                dialog.cancel();
                finish();
                //    startActivity(new Intent(AddStaffDetailActivity.this,AddNewStaffActivity.class));

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    public void showMediaAccess() {
        {
            final CharSequence[] items = {getString(R.string.admin), getString(R.string.editor),
                    getString(R.string.moderator)};
            AlertDialog.Builder alert = new AlertDialog.Builder(AddStaffDetailActivity.this);
            alert.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    btnContinue.setEnabled(true);
                    btnContinue.setAlpha(1.0f);
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
                    btnContinue.setEnabled(true);
                    btnContinue.setAlpha(1.0f);
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

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int i = fm.getBackStackEntryCount();

        if (i > 0) {
            fm.popBackStack();
        }else {

            if (AddStaffServicesActivity.localServiceMap.size() > 0 || isChangeOccured)
                showAlertDailog();
            else {
                finish();
                //   startActivity(new Intent(AddStaffDetailActivity.this, AddNewStaffActivity.class));
            }
        }
    }

}
