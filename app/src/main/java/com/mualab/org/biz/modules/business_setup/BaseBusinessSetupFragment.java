package com.mualab.org.biz.modules.business_setup;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.company_management.ComapnySelectedServices;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.business_setup.my_staff.MyStaffActivity;
import com.mualab.org.biz.modules.business_setup.new_add_staff.AddNewStaffActivity;
import com.mualab.org.biz.modules.business_setup.new_add_staff.adapter.CompanyListSppinnerAdapter;
import com.mualab.org.biz.modules.my_profile.model.UserProfileData;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class BaseBusinessSetupFragment extends Fragment implements View.OnClickListener {
    private String mParam1;
    private Context mContext;
    private TextView tvBusinessName,tvServiceType,tvRatingCount,tvBusiness,tvStaff;
    private RatingBar rating;
    private CircleImageView iv_Profile;
    private ImageView ivActive,ivDropDown;
    private LinearLayout vPanel1;
    private LinearLayout vPanel2;
    private View viewBiz,viewStaff;
    private List<CompanyDetail> companyList;
    private CompanyListSppinnerAdapter arrayAdapter;

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
        ((MainActivity) mContext).setTitle(getString(R.string.title_buisness_admin));
    }

    private void setView(View rootView){
        companyList = new ArrayList<>();
        tvBusinessName =  rootView.findViewById(R.id.tvBusinessName);
        rating =  rootView.findViewById(R.id.rating);
        ivDropDown = rootView.findViewById(R.id.ivDropDown);
        tvRatingCount =  rootView.findViewById(R.id.tvRatingCount);
        tvStaff =  rootView.findViewById(R.id.tvStaff);
        tvServiceType =  rootView.findViewById(R.id.tvServiceType);
        tvBusiness =  rootView.findViewById(R.id.tvBusiness);
        iv_Profile =  rootView.findViewById(R.id.iv_Profile);
        ivActive =  rootView.findViewById(R.id.ivActive);
        vPanel1 =  rootView.findViewById(R.id.vPanel1);
        vPanel2 =  rootView.findViewById(R.id.vPanel2);
        LinearLayout llRow3 = rootView.findViewById(R.id.llRow3);
        viewBiz =  rootView.findViewById(R.id.viewBiz);
        viewStaff =  rootView.findViewById(R.id.viewStaff);


        final Spinner spBizName = rootView.findViewById(R.id.spBizName);

        LinearLayout llAddStaff = rootView.findViewById(R.id.llAddStaff);
        LinearLayout llMyStaff = rootView.findViewById(R.id.llMyStaff);

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // textv1.setText(getString(R.string.business_type));
                // tvBizType.setVisibility(View.GONE);
            }
        });


        tvStaff.setOnClickListener(this);
        tvBusiness.setOnClickListener(this);
        llAddStaff.setOnClickListener(this);
        llMyStaff.setOnClickListener(this);

        apiForGetProfile();

        Session session = Mualab.getInstance().getSessionManager();
        if (session.getUser().businessType.equals("independent")) {
            llRow3.setVisibility(View.VISIBLE);
            apiForCompanyDetail();
        }else
            llRow3.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llMyStaff:
                startActivity(new Intent(mContext,MyStaffActivity.class));
                break;
            case R.id.llAddStaff:
                startActivity(new Intent(mContext,AddNewStaffActivity.class));
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
        User user = session.getUser();

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
                                item.status  = js.getString("status");

                                JSONArray staffHoursArray = object.getJSONArray("staffHours");
                                if (staffHoursArray!=null && staffHoursArray.length()!=0) {
                                    for (int j=0; j<staffHoursArray.length(); j++){
                                        JSONObject object2 = staffHoursArray.getJSONObject(j);
                                        BusinessDayForStaff item2 = new BusinessDayForStaff();
                                        item2.day = Integer.parseInt(object2.getString("day"));
                                        item2.endTime = object2.getString("endTime");
                                        item2.startTime = object2.getString("startTime");
                                        item.staffHoursList.add(item2);
                                    }
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
                            arrayAdapter.notifyDataSetChanged();
                        }else {
                            ivDropDown.setVisibility(View.GONE);
                            tvStaff.setClickable(true);
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


}
