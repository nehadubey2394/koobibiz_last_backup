package com.mualab.org.biz.modules.add_staff.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AddedStaffServices;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.add_staff.SubServices;
import com.mualab.org.biz.modules.add_staff.activity.AllServicesActivity;
import com.mualab.org.biz.modules.add_staff.adapter.ArtistServiceLastAdapter;
import com.mualab.org.biz.modules.add_staff.listner.OnServiceSelectListener;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.KeyboardUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import views.pickerview.MyTimePickerDialog;
import views.pickerview.timepicker.TimePicker;

import static android.app.Activity.RESULT_OK;


public class ArtistLastServicesFragment extends Fragment implements OnServiceSelectListener,View.OnClickListener {
    private ArtistServiceLastAdapter adapter;
    private Context mContext;
    // TODO: Rename and change types of parameters
    private SubServices subServices;
    public static List<SelectedServices> selectedServicesList = new ArrayList<>();
    private StaffDetail staffDetail;
    private User user;
    private ArrayList<ArtistServices> arrayList;

    public ArtistLastServicesFragment() {
        // Required empty public constructor
    }

    public static ArtistLastServicesFragment newInstance(SubServices subServices) {
        ArtistLastServicesFragment fragment = new ArtistLastServicesFragment();
        Bundle args = new Bundle();
        args.putSerializable("param1", subServices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subServices = (SubServices) getArguments().getSerializable("param1");
        }
        if(mContext instanceof AllServicesActivity) {
            staffDetail = ((AllServicesActivity)mContext).getStaffDetail();
            ((AllServicesActivity)mContext).setTitle(subServices.subServiceName);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_last_service, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setViewId(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(){
        Session session = Mualab.getInstance().getSessionManager();
        user = session.getUser();

        arrayList = subServices.artistservices;

        if (staffDetail.staffServices.size()!=0 && selectedServicesList.size()==0){
            if (arrayList.size()!=0){
                for (AddedStaffServices staffServices : staffDetail.staffServices){
                    for (ArtistServices artistServices : arrayList) {
                        if (artistServices._id.equals(staffServices.artistServiceId)) {
                            artistServices.setSelected(true);
                            //add in new arraylist
                            SelectedServices services = new SelectedServices();
                            services.serviceId = subServices.serviceId;
                            services.subserviceId = staffServices.subserviceId;
                            services.artistId = staffServices.staffId;
                            services.businessId = staffServices.businessId;
                            services.artistServiceId = staffServices.artistServiceId;
                            services.inCallPrice = staffServices.inCallPrice;
                            services.outCallPrice = staffServices.outCallPrice;
                            services.completionTime = staffServices.completionTime;
                            selectedServicesList.add(services);
                        }
                    }
                }
            }
        }

        if (staffDetail.staffServices.size()!=0 && selectedServicesList.size()==0){
            for (AddedStaffServices staffServices : staffDetail.staffServices){
                SelectedServices services = new SelectedServices();
                services.serviceId = subServices.serviceId;
                services.subserviceId = staffServices.subserviceId;
                services.artistId = staffServices.staffId;
                services.businessId = staffServices.businessId;
                services.artistServiceId = staffServices.artistServiceId;
                services.inCallPrice = staffServices.inCallPrice;
                services.outCallPrice = staffServices.outCallPrice;
                services.completionTime = staffServices.completionTime;
                selectedServicesList.add(services);
            }
        }

        if (selectedServicesList.size()>0){
            for (SelectedServices selectedServices : selectedServicesList){
                for (ArtistServices artistServices : arrayList) {
                    if (artistServices._id.equals(selectedServices.artistServiceId)) {
                        artistServices.setSelected(true);
                    }
                }
            }
        }

        adapter = new ArtistServiceLastAdapter(mContext, arrayList);

    }

    private void setViewId(View rootView){

        AppCompatButton btnAddMoreService = rootView.findViewById(R.id.btnAddMoreService);
        AppCompatButton btnDone = rootView.findViewById(R.id.btnDone);

        RecyclerView rvLastService = rootView.findViewById(R.id.rvLastService);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvLastService.setLayoutManager(layoutManager);

        rvLastService.setAdapter(adapter);
        adapter.setListener(ArtistLastServicesFragment.this);

        btnAddMoreService.setOnClickListener(this);
        btnDone.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, ArtistServices artistServices) {

        if (artistServices.isSelected()){
            MyToast.getInstance(mContext).showDasuAlert("This service is already added,Select another service");
        }else {
            showDetailDialog(artistServices,position);
        }
    }

    private void showDetailDialog(final ArtistServices artistServices, final int position){
        final View DialogView = View.inflate(mContext, R.layout.dialog_layout_service_detail, null);


        final Dialog dialog = new Dialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(DialogView);
        EditText etInCallPrice = DialogView.findViewById(R.id.etInCallPrice);
        EditText etOutCallPrice = DialogView.findViewById(R.id.etOutCallPrice);
        final TextView tvCTime = DialogView.findViewById(R.id.tvCTime);
        TextView tvTile = DialogView.findViewById(R.id.tvTile);
        LinearLayout llCtime = DialogView.findViewById(R.id.llCtime);
        AppCompatButton btnDone = DialogView.findViewById(R.id.btnDone);
        AppCompatButton btnCancel = DialogView.findViewById(R.id.btnCancel);

        tvTile.setText(artistServices.title);
        etInCallPrice.setText(artistServices.inCallPrice);
        etOutCallPrice.setText(artistServices.outCallPrice);
        tvCTime.setText(artistServices.completionTime);
        etInCallPrice.clearFocus();
        etOutCallPrice.clearFocus();

        llCtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicker(tvCTime,getString(R.string.time_for_completion));
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cTime = tvCTime.getText().toString().trim();
                artistServices.setSelected(true);
                adapter.notifyDataSetChanged();
                SelectedServices services = new SelectedServices();
                services.serviceId = subServices.serviceId;
                services.subserviceId = subServices.subServiceId;
                services.artistId = staffDetail.staffId;
                services.businessId = String.valueOf(user.id);
                services.artistServiceId = artistServices._id;
                services.inCallPrice = artistServices.inCallPrice;
                services.outCallPrice = artistServices.outCallPrice;
                services.completionTime = cTime;
                selectedServicesList.add(services);

                ArtistServices newService = arrayList.get(position);
                newService.completionTime = services.completionTime;
                newService.inCallPrice = services.inCallPrice;
                newService.outCallPrice = services.outCallPrice;
                arrayList.set(position,newService);
                adapter.notifyDataSetChanged();
                KeyboardUtil.hideKeyboard(DialogView,mContext);
                dialog.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void showPicker(final TextView tvTime, final String title){
        int hours = 01;
        //int minute = 00;
        String tmpTime = tvTime.getText().toString();
        if(!tmpTime.equals("HH:MM")){
            String[] arrayTime = tmpTime.split(":");
            hours = Integer.parseInt(arrayTime[0]);
            // minute = Integer.parseInt(arrayTime[1]);
        }
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(mContext, new MyTimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hours, int minute) {
                tvTime.setText(String.format("%s:%s", String.format("%02d", hours), String.format("%02d", minute)));
            }
        }, title, hours, 00,10);
        mTimePicker.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnDone:
                LinkedHashSet<SelectedServices> listToSet = new LinkedHashSet<>(selectedServicesList);
                selectedServicesList.clear();
                selectedServicesList.addAll(listToSet);

                String[] sIds = new String[selectedServicesList.size()];
                for (int i = 0;i<selectedServicesList.size();i++){
                    SelectedServices  services = selectedServicesList.get(i);
                    sIds[i] = services.artistServiceId;
                }

                Gson gson = new GsonBuilder().create();
                JsonArray jsonArray = gson.toJsonTree(selectedServicesList).getAsJsonArray();

                if (jsonArray!=null && jsonArray.size()!=0) {
                    apiForAddServices(jsonArray,sIds);
                }
                break;
            case R.id.btnAddMoreService:
                clearBackStack();
                break;
        }
    }

    private void apiForAddServices(final JsonArray jsonArray,final String[] sIds){
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForAddServices(jsonArray,sIds);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staffDetail.staffId);
        params.put("businessId", String.valueOf(user.id));
        params.put("staffService", jsonArray.toString());

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "addStaffService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        Intent intent = new Intent();
                        intent.putExtra("jsonArray", sIds);
                        //  selectedServicesList.clear();
                        ((AllServicesActivity)mContext).setResult(RESULT_OK, intent);
                        ((AllServicesActivity)mContext).finish();

                    }else {
                        if (!message.equals("Service already added"))
                            MyToast.getInstance(mContext).showDasuAlert(message);
                    }
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

    private void clearBackStack() {
        FragmentManager fm = ((AllServicesActivity)mContext).getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}