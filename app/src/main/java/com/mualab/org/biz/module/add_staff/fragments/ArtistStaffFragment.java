package com.mualab.org.biz.module.add_staff.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.module.add_staff.SearchStaffActivity;
import com.mualab.org.biz.module.add_staff.adapter.ArtistStaffAdapter;
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


public class ArtistStaffFragment extends Fragment implements View.OnClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private TextView tvNoDataFound;
    private Context mContext;
    private List<Staff>artistStaffs;
    private ArtistStaffAdapter staffAdapter;
    private RecyclerView rvArtistStaff;

    public ArtistStaffFragment() {
        // Required empty public constructor
    }


    public static ArtistStaffFragment newInstance(String param1, String param2) {
        ArtistStaffFragment fragment = new ArtistStaffFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
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
        artistStaffs = new ArrayList<>();
        staffAdapter = new ArtistStaffAdapter(mContext, artistStaffs);


        rvArtistStaff = rootView.findViewById(R.id.rvArtistStaff);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvArtistStaff.setLayoutManager(layoutManager);
        rvArtistStaff.setAdapter(staffAdapter);

        tvNoDataFound = rootView.findViewById(R.id.tvNoDataFound);
        LinearLayout llAddStaff = rootView.findViewById(R.id.llAddStaff);

        llAddStaff.setOnClickListener(this);
        apiForGetArtistStaff();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.llAddStaff:
                startActivity(new Intent(mContext, SearchStaffActivity.class));
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
                                Staff item = new Staff();
                                JSONObject object = jsonArray.getJSONObject(i);
                                item._id = object.getString("_id");
                                item.staffId = object.getString("_id");
                                item.staffName = object.getString("staffName");
                                item.staffImage = object.getString("staffImage");
                                item.job = object.getString("job");

                                artistStaffs.add(item);
                            }
                        }else {
                            rvArtistStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                        staffAdapter.notifyDataSetChanged();

                     /*   //parsing for getting staff
                        JSONArray arrStaffDetail= js.getJSONArray("staffDetail");
                        if (arrStaffDetail!=null && arrStaffDetail.length()!=0) {
                            for (int l=0; l<arrStaffDetail.length(); l++){
                                JSONObject object = arrStaffDetail.getJSONObject(l);
                                Staff staff = new Staff();
                                staff.staffId = object.getString("_id");
                                staff.staffName = object.getString("staffName");
                                staff.staffImage = object.getString("staffImage");
                                staff.isSelected = false;
                                staff.job = object.getString("job");
                                staffList.add(staff);
                            }
                        }*/

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
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
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

}
