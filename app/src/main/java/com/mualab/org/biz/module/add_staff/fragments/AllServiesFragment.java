package com.mualab.org.biz.module.add_staff.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.Services;
import com.mualab.org.biz.model.add_staff.SubServices;
import com.mualab.org.biz.module.add_staff.activity.AllServicesActivity;
import com.mualab.org.biz.module.add_staff.adapter.ServiceExpandListAdapter;
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


public class AllServiesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Context mContext;
    private ExpandableListView lvExpandable;
    private TextView tvNoData;
    private ServiceExpandListAdapter expandableListAdapter;
    private List<Services> servicesList;

    public AllServiesFragment() {
        // Required empty public constructor
    }


    public static AllServiesFragment newInstance(String param1) {
        AllServiesFragment fragment = new AllServiesFragment();
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
        return inflater.inflate(R.layout.fragment_all_services, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View rootView){
        servicesList = new ArrayList<>();
        expandableListAdapter = new ServiceExpandListAdapter(mContext, servicesList);
        setView(rootView);
    }

    private void setView(View rootView){
        lvExpandable = rootView.findViewById(R.id.lvExpandable);
        tvNoData = rootView.findViewById(R.id.tvNoData);
        lvExpandable.setAdapter(expandableListAdapter);

        lvExpandable.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {

            }
        });

        lvExpandable.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Services item = servicesList.get(groupPosition);
                item.isExpand = true;
            }
        });

        // Listview Group collasped listener
        lvExpandable.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Services item = servicesList.get(groupPosition);
                item.isExpand = false;

            }
        });

        lvExpandable.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return false;
            }
        });

        lvExpandable.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                onChildClickListener(expandableListView,view,groupPosition,childPosition,l);
                return false;
            }
        });

        apiForGetAllServices();
    }
    private void onChildClickListener(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l){
        Services servicesItem = servicesList.get(groupPosition);
        SubServices subServices = servicesItem.arrayList.get(childPosition);
        ((AllServicesActivity)mContext).addFragment(ArtistLastServicesFragment.newInstance(subServices), true, R.id.flServiceContainer);
    }

    private void apiForGetAllServices(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetAllServices();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "artistService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        lvExpandable.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        servicesList.clear();
                        JSONArray allServiceArray = js.getJSONArray("artistServices");
                        if (allServiceArray!=null) {
                            for (int j=0; j<allServiceArray.length(); j++){
                                JSONObject object = allServiceArray.getJSONObject(j);
                                Services services = new Services();
                                services.serviceId = object.getString("serviceId");
                                services.serviceName = object.getString("serviceName");

                                JSONArray subServiesArray = object.getJSONArray("subServies");
                                if (subServiesArray!=null) {
                                    for (int k=0; k<subServiesArray.length(); k++){
                                        JSONObject jObj = subServiesArray.getJSONObject(k);
                                        Gson gson = new Gson();
                                        SubServices subServices = gson.fromJson(String.valueOf(jObj), SubServices.class);
                                       /* SubServices subServices = new SubServices();
                                        subServices._id = jObj.getString("_id");
                                        subServices.serviceId = jObj.getString("serviceId");
                                        subServices.subServiceId = jObj.getString("subServiceId");
                                        subServices.subServiceName = jObj.getString("subServiceName");*/

                                        JSONArray artistservices = jObj.getJSONArray("artistService");
                                        for (int m=0; m<artistservices.length(); m++){
                                            JSONObject jsonObject3 = artistservices.getJSONObject(m);
                                            Gson gson2 = new Gson();
                                            ArtistServices services3 = gson2.fromJson(String.valueOf(jsonObject3), ArtistServices.class);
                                            if (!services3.outCallPrice.equals("0") || !services3.outCallPrice.equals("null")){
                                                services3.isOutCall3 = true;
                                                subServices.isOutCall2 = true;
                                                services.isOutCall = true;
                                            }
                                          /*  ArtistServices services3 = new ArtistServices();
                                            services3._id = jsonObject3.getString("_id");
                                            services3.setSelected(false);
                                            services3.setBooked(false);
                                            services3.title = jsonObject3.getString("title");
                                            services3.completionTime = jsonObject3.getString("completionTime");
                                            services3.outCallPrice = jsonObject3.getString("outCallPrice");
                                            services3.inCallPrice = jsonObject3.getString("inCallPrice");

                                            if (!services3.outCallPrice.equals("0") || !services3.outCallPrice.equals("null")){
                                                services3.isOutCall3 = true;
                                                subServices.isOutCall2 = true;
                                                services.isOutCall = true;
                                            }*/
                                            subServices.artistservices.add(services3);
                                        }

                                        services.arrayList.add(subServices);
                                    }
                                }
                                servicesList.add(services);
                            }
                            expandableListAdapter.notifyDataSetChanged();
                        }
                    }else {
                        lvExpandable.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
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
        task.execute(this.getClass().getName());
    }

}
