package com.mualab.org.biz.modules.business_setup;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.Address;
import com.mualab.org.biz.model.BusinessDay;
import com.mualab.org.biz.model.BusinessProfile;
import com.mualab.org.biz.model.TimeSlot;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.company_management.ComapnySelectedServices;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.business_setup.OtherBusinessWorkingHours.OperationHoursActivity;
import com.mualab.org.biz.modules.business_setup.Services.CompanyServicesActivity;
import com.mualab.org.biz.modules.business_setup.business_info.BusinessInfoActivity;
import com.mualab.org.biz.modules.business_setup.certificate.AllCertificatesActivity;
import com.mualab.org.biz.modules.business_setup.invitation.InvitationActivity;
import com.mualab.org.biz.modules.business_setup.my_staff.MyStaffActivity;
import com.mualab.org.biz.modules.business_setup.new_add_staff.AddNewStaffActivity;
import com.mualab.org.biz.modules.business_setup.new_add_staff.adapter.CompanyListSppinnerAdapter;
import com.mualab.org.biz.modules.my_profile.model.UserProfileData;
import com.mualab.org.biz.modules.profile_setup.activity.WorkingHoursActivity;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class BaseBusinessSetupFragment extends Fragment implements View.OnClickListener {
    private String mParam1;
    private Context mContext;
    private TextView tvBusinessName,tvServiceType,tvRatingCount,tvBusiness,tvStaff,
            tvHeaderTitle;
    private RatingBar rating;
    private CircleImageView iv_Profile;
    private ImageView ivActive,ivDropDown;
    private LinearLayout vPanel1;
    private LinearLayout vPanel2;
    private LinearLayout llRow3,llRow2,llTabs;
    private RelativeLayout llLineView;
    private View viewBiz,viewStaff,line2;
    private List<CompanyDetail> companyList;
    private CompanyListSppinnerAdapter arrayAdapter;
    private int currentSelectedBusiness;
    private  Session session;
    private User user;

    public BaseBusinessSetupFragment() {
        // Required empty public constructor
    }

    public static BaseBusinessSetupFragment newInstance(String param1) {
        BaseBusinessSetupFragment fragment = new BaseBusinessSetupFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_business_setup, container, false);

        initView();

        setView(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(){
        session = Mualab.getInstance().getSessionManager();
        user = session.getUser();
        ((MainActivity) mContext).setTitle(getString(R.string.title_buisness_admin));
    }

    private void setView(View rootView){
        companyList = new ArrayList<>();
        tvBusinessName =  rootView.findViewById(R.id.tvBusinessName);
        rating =  rootView.findViewById(R.id.rating);
        // ivDropDown = rootView.findViewById(R.id.ivDropDown);
        // tvHeaderTitle =  rootView.findViewById(R.id.tvHeaderTitle);
        tvRatingCount =  rootView.findViewById(R.id.tvRatingCount);
        tvStaff =  rootView.findViewById(R.id.tvStaff);
        tvServiceType =  rootView.findViewById(R.id.tvServiceType);
        tvBusiness =  rootView.findViewById(R.id.tvBusiness);
        iv_Profile =  rootView.findViewById(R.id.iv_Profile);
        ivActive =  rootView.findViewById(R.id.ivActive);
        vPanel1 =  rootView.findViewById(R.id.vPanel1);
        vPanel2 =  rootView.findViewById(R.id.vPanel2);
        llRow3 = rootView.findViewById(R.id.llRow3);
        llRow2 = rootView.findViewById(R.id.llRow2);
        llTabs = rootView.findViewById(R.id.llTabs);
        llLineView = rootView.findViewById(R.id.llLineView);
        viewBiz =  rootView.findViewById(R.id.viewBiz);
        viewStaff =  rootView.findViewById(R.id.viewStaff);
        line2 =  rootView.findViewById(R.id.line2);

        final Spinner spBizName = ((MainActivity) mContext).findViewById(R.id.spBizName);
        ivDropDown= ((MainActivity) mContext).findViewById(R.id.ivDropDown);
        tvHeaderTitle= ((MainActivity) mContext).findViewById(R.id.tvHeaderTitle);

        LinearLayout llAddStaff = rootView.findViewById(R.id.llAddStaff);
        LinearLayout llMyStaff = rootView.findViewById(R.id.llMyStaff);
        LinearLayout llWorkingHours = rootView.findViewById(R.id.llWorkingHours);
        LinearLayout llServices = rootView.findViewById(R.id.llServices);
        LinearLayout llBusinessInfo = rootView.findViewById(R.id.llBusinessInfo);
        LinearLayout llInvitation = rootView.findViewById(R.id.llInvitation);
        LinearLayout llCertificates = rootView.findViewById(R.id.llCertificates);
        LinearLayout llVoucherCode = rootView.findViewById(R.id.llVoucherCode);
        LinearLayout llPaymentSeup = rootView.findViewById(R.id.llPaymentSeup);

        arrayAdapter = new CompanyListSppinnerAdapter(mContext,
                companyList);

        spBizName.setAdapter(arrayAdapter);
        spBizName.setPrompt("");
        spBizName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spBizName.setPrompt("");
                TextView textView =  view.findViewById(R.id.tvSpItem);
                textView.setText("");
                tvHeaderTitle.setText(companyList.get(i).businessName);
                currentSelectedBusiness = i;

                if (i==0) {
                    llRow2.setVisibility(View.VISIBLE);
                    llRow3.setVisibility(View.VISIBLE);
                    llTabs.setVisibility(View.VISIBLE);
                    llLineView.setVisibility(View.VISIBLE);
                    line2.setVisibility(View.GONE);
                }else {
                    llRow2.setVisibility(View.GONE);
                    llRow3.setVisibility(View.GONE);
                    llTabs.setVisibility(View.GONE);
                    llLineView.setVisibility(View.GONE);
                    line2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // textv1.setText(getString(R.string.business_type));
                // tvBizType.setVisibility(View.GONE);
            }
        });


        tvStaff.setOnClickListener(this);
        tvBusiness.setOnClickListener(this);
        llInvitation.setOnClickListener(this);
        llAddStaff.setOnClickListener(this);
        llMyStaff.setOnClickListener(this);
        llServices.setOnClickListener(this);
        llWorkingHours.setOnClickListener(this);
        llBusinessInfo.setOnClickListener(this);
        llCertificates.setOnClickListener(this);
        llVoucherCode.setOnClickListener(this);
        llPaymentSeup.setOnClickListener(this);

        apiForGetProfile();

        getBusinessProfile();

        Session session = Mualab.getInstance().getSessionManager();
        if (session.getUser().businessType.equals("independent")) {
            llRow3.setVisibility(View.VISIBLE);
            apiForCompanyDetail();
        }else
            llRow3.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        switch (view.getId()){
            case R.id.llBusinessInfo:

                Intent intent2 = new Intent(mContext, BusinessInfoActivity.class);
                if (currentSelectedBusiness==0) {
                    MyToast.getInstance(mContext).showDasuAlert("Under development");
                    //  intent2.putExtra("businessId",String.valueOf(user.id));
                }else {
                    if (companyList.size()!=0) {
                        intent2.putExtra("businessId", companyList.get(currentSelectedBusiness).businessId);
                        startActivity(intent2);
                    }
                }
                break;

            case R.id.llServices:
                Intent intent3 = new Intent(mContext, CompanyServicesActivity.class);
                if (currentSelectedBusiness==0) {
                    MyToast.getInstance(mContext).showDasuAlert("Under development");
                }else {
                    if (companyList.size()!=0) {
                        intent3.putExtra("businessId", companyList.get(currentSelectedBusiness).businessId);
                        intent3.putExtra("staffId", companyList.get(currentSelectedBusiness)._id);
                        startActivity(intent3);
                    }
                }
                break;

            case R.id.llWorkingHours:
                if (currentSelectedBusiness==0) {
                    Intent intent = new Intent(mContext, WorkingHoursActivity.class);
                    intent.putExtra("fromTag", "BaseBusinessSetupFragment");
                    startActivity(intent);

                }else {
                    if (companyList.size()!=0) {
                        Intent intent = new Intent(mContext, OperationHoursActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable("workingHours",(Serializable)companyList.get(currentSelectedBusiness).businessDays);
                        intent.putExtra("BUNDLE",args);
                        startActivity(intent);
                    }

                }
                break;

            case R.id.llMyStaff:
                startActivity(new Intent(mContext,MyStaffActivity.class));
                break;
            case R.id.llAddStaff:
                startActivity(new Intent(mContext,AddNewStaffActivity.class));
                break;

            case R.id.llInvitation:
                startActivity(new Intent(mContext,InvitationActivity.class));
                break;

            case R.id.llCertificates:
                startActivity(new Intent(mContext,AllCertificatesActivity.class));
                break;

            case R.id.tvBusiness:
                vPanel2.setVisibility(View.GONE);
                vPanel1.setVisibility(View.VISIBLE);
                viewBiz.setVisibility(View.VISIBLE);
                viewStaff.setVisibility(View.INVISIBLE);
                tvBusiness.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvStaff.setTextColor(getResources().getColor(R.color.gray));
                //     viewStaff.setBackgroundColor(getResources().getColor(R.color.gray_line));
                //     viewBiz.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                break;

            case R.id.tvStaff:
                vPanel2.setVisibility(View.VISIBLE);
                //       viewStaff.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                viewStaff.setVisibility(View.VISIBLE);
                vPanel1.setVisibility(View.GONE);
                //   viewBiz.setBackgroundColor(getResources().getColor(R.color.gray_line));
                viewBiz.setVisibility(View.INVISIBLE);
                tvStaff.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvBusiness.setTextColor(getResources().getColor(R.color.gray));
                break;
        }
    }

    private void apiForGetProfile(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetProfile();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(user.id));
        params.put("loginUserId", String.valueOf(user.id));
        // params.put("viewBy", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "getProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                Progress.hide(mContext);
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    Progress.hide(mContext);
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray userDetail = js.getJSONArray("userDetail");
                        JSONObject object = userDetail.getJSONObject(0);
                        Gson gson = new Gson();

                        UserProfileData profileData = gson.fromJson(String.valueOf(object), UserProfileData.class);

                        setProfileData(profileData);

                    }else {
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    }

                } catch (Exception e) {
                    Progress.hide(mContext);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                Progress.hide(mContext);
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showSmallCustomToast(helper.error_Messages(error));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(getClass().getName());
    }

    private void apiForCompanyDetail(){
        Session session = Mualab.getInstance().getSessionManager();
        final User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForCompanyDetail();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "companyInfo", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        companyList.clear();
                        ivDropDown.setVisibility(View.VISIBLE);

                        CompanyDetail item1 = new CompanyDetail();
                        item1.businessName = getString(R.string.title_buisness_admin);
                        companyList.add(item1);

                        JSONArray jsonArray = js.getJSONArray("businessList");

                        if (jsonArray!=null && jsonArray.length()!=0) {
                            tvStaff.setClickable(false);

                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                CompanyDetail item = new CompanyDetail();
                                item._id = object.getString("_id");
                                item.artistId = object.getString("artistId");
                                item.businessId = object.getString("businessId");
                                item.holiday = object.getString("holiday");
                                item.job = object.getString("job");
                                item.mediaAccess = object.getString("mediaAccess");
                                item.businessName = object.getString("businessName");
                                item.profileImage = object.getString("profileImage");
                                item.address = object.getString("address");
                                item.userName = object.getString("userName");
                                item.status  = object.getString("status");

                                JSONArray staffHoursArray = object.getJSONArray("staffHours");

                                BusinessProfile bsp = new BusinessProfile();

                                if (staffHoursArray!=null && staffHoursArray.length()!=0) {
                                    bsp.businessDays = getBusinessDay();

                                    Set<BusinessDay> daySet = new HashSet<>();

                                    for (int j=0; j<staffHoursArray.length(); j++){
                                        JSONObject object2 = staffHoursArray.getJSONObject(j);
                                        BusinessDayForStaff item2 = new BusinessDayForStaff();

                                        BusinessDay day = new BusinessDay();
                                        int dayId = object2.getInt("day");
                                        TimeSlot slot = new TimeSlot(dayId);
                                        slot.startTime = object2.getString("startTime");
                                        slot.minStartTime = object2.getString("startTime");
                                        slot.endTime = object2.getString("endTime");
                                        slot.maxEndTime = object2.getString("endTime");

                                        item2.day = Integer.parseInt(object2.getString("day"));
                                        item2.endTime = object2.getString("endTime");
                                        item2.startTime = object2.getString("startTime");
                                        item.staffHoursList.add(item2);

                                        for(BusinessDay tmpDay : bsp.businessDays){
                                            if(tmpDay.dayId == dayId){
                                                tmpDay.isOpen = true;
                                                day.isExpand = true;
                                              /*  if (dayId==0) {
                                                    day.dayName = getString(R.string.monday);
                                                }
                                                else if (dayId==1) {
                                                    day.dayName = getString(R.string.tuesday);
                                                }
                                                else if (dayId==2) {
                                                    day.dayName = getString(R.string.wednesday);
                                                }
                                                else if (dayId==3) {
                                                    day.dayName = getString(R.string.thursday);
                                                }
                                                else if (dayId==4) {
                                                    day.dayName = getString(R.string.frieday);
                                                }
                                                else    if (dayId==5) {
                                                    day.dayName = getString(R.string.saturday);
                                                }
                                                else if (dayId==6) {
                                                    day.dayName = getString(R.string.sunday);
                                                }*/
                                                tmpDay.addTimeSlot(slot);
                                                daySet.add(tmpDay);
                                                // item.businessDays.add(tmpDay);
                                                break;
                                            }
                                        }
                                    }

                                    item.businessDays.addAll(daySet);

                                    Collections.sort(item.businessDays, new Comparator<BusinessDay>() {

                                        @Override
                                        public int compare(BusinessDay a1, BusinessDay a2) {

                                            if (a1.dayId == 0 || a2.dayId == 0)
                                                return -1;
                                            else {
                                                Long long1 = (long) a1.dayId;
                                                Long long2 = (long) a2.dayId;
                                                return long1.compareTo(long2);
                                            }
                                        }
                                    });
                                }

                                JSONArray staffServiceArray = object.getJSONArray("staffService");
                                for (int k=0; k<staffServiceArray.length(); k++){
                                    JSONObject object3 = staffServiceArray.getJSONObject(k);
                                    Gson gson = new Gson();
                                    ComapnySelectedServices item3 = gson.fromJson(String.valueOf(object3), ComapnySelectedServices.class);
                                    item.staffService.add(item3);
                                }

                                if (item.status.equals("1"))
                                    companyList.add(item);
                            }

                            if (companyList.size()==0)
                                ivDropDown.setVisibility(View.GONE);

                            arrayAdapter.notifyDataSetChanged();
                        }else {
                            ivDropDown.setVisibility(View.GONE);
                            tvStaff.setClickable(true);
                        }

                    }else {
                        ivDropDown.setVisibility(View.GONE);
                        tvStaff.setClickable(true);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(mContext);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                        // MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private List<BusinessDay> getBusinessDay(){

        List<BusinessDay>businessDays = new ArrayList<>();

        BusinessDay day1 = new BusinessDay();
        day1.dayName = getString(R.string.monday);
        day1.dayId = 0;
        //day1.addTimeSlot(new TimeSlot(1));

        BusinessDay day2 = new BusinessDay();
        day2.dayName = getString(R.string.tuesday);
        day2.dayId = 1;

        BusinessDay day3 = new BusinessDay();
        day3.dayName = getString(R.string.wednesday);
        day3.dayId = 2;

        BusinessDay day4 = new BusinessDay();
        day4.dayName = getString(R.string.thursday);
        day4.dayId = 3;

        BusinessDay day5 = new BusinessDay();
        day5.dayName = getString(R.string.frieday);
        day5.dayId = 4;

        BusinessDay day6 = new BusinessDay();
        day6.dayName = getString(R.string.saturday);
        day6.dayId = 5;

        BusinessDay day7 = new BusinessDay();
        day7.dayName = getString(R.string.sunday);
        day7.dayId = 6;

        businessDays.add(day1);
        businessDays.add(day2);
        businessDays.add(day3);
        businessDays.add(day4);
        businessDays.add(day5);
        businessDays.add(day6);
        businessDays.add(day7 );
        return businessDays;
    }

    private void setProfileData(UserProfileData profileData){
        if (profileData!=null){
            tvRatingCount.setText("("+profileData.reviewCount+")");
            tvBusinessName.setText(profileData.userName);
            tvServiceType.setText(profileData.serviceType);
            rating.setRating(Float.parseFloat(profileData.ratingCount));
            switch (profileData.serviceType) {
                case "1":
                    tvServiceType.setText("Incall");
                    break;
                case "2":
                    tvServiceType.setText("Outcall");
                    break;
                case "3":
                    tvServiceType.setText("Both");
                    break;
            }


            if (profileData.isCertificateVerify.equals("0")){
                ivActive.setVisibility(View.GONE);
            }else
                ivActive.setVisibility(View.VISIBLE);

            if (!profileData.profileImage.equals(""))
                Picasso.with(mContext).load(profileData.profileImage).placeholder(R.drawable.defoult_user_img).
                        fit().into(iv_Profile);

        }

    }

    private void getBusinessProfile(){
        //progress_bar.setVisibility(View.VISIBLE);
        new HttpTask(new HttpTask.Builder(mContext, "getbusinessProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    // progress_bar.setVisibility(View.GONE);

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("artistRecord");

                    if(jsonArray.length()>0){
                        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
                        JSONObject obj = jsonArray.getJSONObject(0);
                        BusinessProfile bsp = new BusinessProfile();

                        bsp.address = new Address();
                        bsp.address.postalCode = obj.getString("businesspostalCode");
                        bsp.address.stAddress1 = obj.getString("address");
                        bsp.address.stAddress2 = obj.getString("address2");
                        bsp.address.city = obj.getString("city");
                        bsp.address.state = obj.getString("state");
                        bsp.address.country = obj.getString("country");
                        bsp.address.latitude = obj.getString("latitude");
                        bsp.address.longitude = obj.getString("longitude");

                        bsp.bio = obj.getString("bio");
                        bsp.bankStatus = obj.getInt("bankStatus");

                        if(obj.has("radius")){
                            String r = obj.getString("radius");
                            if(!TextUtils.isEmpty(r))
                                bsp.radius = obj.getInt("radius");
                        }

                        if(obj.has("serviceType"))
                            bsp.serviceType = obj.getInt("serviceType");
                        if(obj.has("inCallpreprationTime"))
                            bsp.inCallpreprationTime = obj.getString("inCallpreprationTime");
                        if(obj.has("outCallpreprationTime"))
                            bsp.outCallpreprationTime = obj.getString("outCallpreprationTime");

                        pSession.updateRadius(bsp.radius);
                        pSession.updateAddress(bsp.address);
                        pSession.updateOutcallPreprationTime(bsp.outCallpreprationTime);
                        pSession.updateIncallPreprationTime(bsp.inCallpreprationTime);
                        pSession.updateServiceType(bsp.serviceType);
                        pSession.updateBankStatus(bsp.bankStatus);

                        // List<BusinessDayForStaff>dayArrayList = new ArrayList<>();

                        JSONArray businessArray = obj.getJSONArray("businessHour");
                        bsp.businessDays = getBusinessDay();

                        for(int i =0; i<businessArray.length();  i++){
                            BusinessDayForStaff businessDayForStaff = new BusinessDayForStaff();
                            JSONObject objSlots = businessArray.getJSONObject(i);
                            int dayId = objSlots.getInt("day");
                            TimeSlot slot = new TimeSlot(dayId);
                            slot.id = objSlots.getInt("_id");
                            slot.startTime = objSlots.getString("startTime");
                            slot.endTime = objSlots.getString("endTime");

                            slot.minStartTime = objSlots.getString("startTime");
                            slot.maxEndTime = objSlots.getString("endTime");

                            slot.edtStartTime = objSlots.getString("startTime");

                            slot.edtEndTime = objSlots.getString("endTime");
                            slot.status = objSlots.getInt("status");

                            businessDayForStaff.day = objSlots.getInt("day");
                            businessDayForStaff.startTime = objSlots.getString("startTime");
                            businessDayForStaff.endTime = objSlots.getString("endTime");
                            bsp.dayForStaffs.add(businessDayForStaff);

                            for(BusinessDay tmpDay : bsp.businessDays){
                                if(tmpDay.dayId == dayId){
                                    tmpDay.isOpen = true;
                                    tmpDay.addTimeSlot(slot);
                                    break;
                                }
                            }
                        }

                        if(businessArray.length()>0)
                            pSession.createBusinessProfile(bsp);

                        PreRegistrationSession bpSession = Mualab.getInstance().getBusinessProfileSession();
                        bpSession.updateRegStep(6);
                    }



                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }

            @Override
            public void ErrorListener(VolleyError error) {

                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }})
                .setMethod(Request.Method.GET)
                .setAuthToken(session.getAuthToken()))
                .execute("getbusinessProfile");
    }


}
