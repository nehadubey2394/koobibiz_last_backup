package com.mualab.org.biz.modules.add_staff.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.add_staff.activity.AddStaffActivity;
import com.mualab.org.biz.modules.add_staff.adapter.ArtistStaffAdapter;
import com.mualab.org.biz.modules.add_staff.listner.OnBottomReachedListener;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArtistStaffFragment extends Fragment implements View.OnClickListener,
        ArtistStaffAdapter.OnDeleteStaffListener,OnBottomReachedListener {
    private TextView tvNoDataFound;
    private Context mContext;
    private List<Staff>artistStaffs;
    private ArtistStaffAdapter staffAdapter;
    private RecyclerView rvArtistStaff;
    private   long mLastClickTime = 0;

    public ArtistStaffFragment() {
        // Required empty public constructor
    }


    public static ArtistStaffFragment newInstance(String param1) {
        ArtistStaffFragment fragment = new ArtistStaffFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View rootView){
        // if(mContext instanceof AddStaffActivity) {
        //   ((AddStaffActivity) mContext).setTitle();
        artistStaffs = new ArrayList<>();
        staffAdapter = new ArtistStaffAdapter(mContext, artistStaffs);

        rvArtistStaff = rootView.findViewById(R.id.rvArtistStaff);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvArtistStaff.setLayoutManager(layoutManager);
        //  rvArtistStaff.setNestedScrollingEnabled(false);
        rvArtistStaff.setAdapter(staffAdapter);
        staffAdapter.setChangeListener(ArtistStaffFragment.this);
        staffAdapter.setOnBottomReachedListener(ArtistStaffFragment.this);

        tvNoDataFound = rootView.findViewById(R.id.tvNoDataFound);
        LinearLayout llAddStaff = rootView.findViewById(R.id.llAddStaff);

        llAddStaff.setOnClickListener(this);
        apiForGetArtistStaff();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.llAddStaff:
                //startActivity(new Intent(mContext, SearchStaffActivity.class));
                ((AddStaffActivity)mContext).addFragment(
                        SearchStaffFragment.newInstance(""), true,R.id.flStffContainer);
                break;
        }
    }

    private void apiForGetArtistStaff(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetArtistStaff();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "artistStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        artistStaffs.clear();
                        rvArtistStaff.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.GONE);

                        JSONArray jsonArray = js.getJSONArray("staffList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                // Staff item = new Staff();
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                Staff item = gson.fromJson(String.valueOf(object), Staff.class);

                                artistStaffs.add(item);
                            }
                        }else {
                            rvArtistStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                        staffAdapter.notifyDataSetChanged();
                    }else {
                        rvArtistStaff.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
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
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    @Override
    public void onStaffSelect(int position, Staff staff) {
        apiForDeleteStaff(staff);
    }

    private void apiForDeleteStaff(final Staff staff){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteStaff(staff);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("staffId", staff.staffId);
        params.put("businessId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "deleteStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        if (artistStaffs.size()!=0){
                            artistStaffs.remove(staff);
                            staffAdapter.notifyDataSetChanged();
                        }

                        if (artistStaffs.size()==0)
                        {
                            rvArtistStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                        MyToast.getInstance(mContext).showDasuAlert(message);
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

        task.execute(this.getClass().getName());
    }

    @Override
    public void onBottomReached(int position) {
    }
}
