package com.mualab.org.biz.modules.old_company_management.fragments;


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
import com.mualab.org.biz.model.company_management.ComapnySelectedServices;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.add_staff.adapter.ServiceExpandListAdapter;
import com.mualab.org.biz.modules.old_company_management.activity.CompanyServicesActivity;
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


public class ServiesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private Context mContext;
    private ExpandableListView lvExpandable;
    private TextView tvNoData;
    private ServiceExpandListAdapter expandableListAdapter;
    private List<Services> servicesList;
    private HashMap<Integer, Services> serviceHashMap ;

    public ServiesFragment() {
        // Required empty public constructor
    }


    public static ServiesFragment newInstance(String param1) {
        ServiesFragment fragment = new ServiesFragment();
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
        serviceHashMap = new HashMap<>();
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
        ((CompanyServicesActivity)mContext).addFragment(LastServicesFragment.newInstance(subServices), true, R.id.flServiceContainer);
    }

    private void apiForGetAllServices(){
        final CompanyDetail companyDetail = ((CompanyServicesActivity)mContext).getCompanyDetail();

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
        params.put("artistId", companyDetail.businessId );

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
                        JSONArray allServiceArray = js.getJSONArray("artistServices");
                        if (allServiceArray!=null) {
                            List<Services> list = new ArrayList<>();

                            for (int j=0; j<allServiceArray.length(); j++){
                                JSONObject object = allServiceArray.getJSONObject(j);
                                Services services = new Services();
                                services.serviceId = object.getString("serviceId");
                                services.serviceName = object.getString("serviceName");

                                JSONArray subServiesArray = object.getJSONArray("subServies");
                                if (subServiesArray!=null) {
                                    for (int k=0; k<subServiesArray.length(); k++){
                                        JSONObject jObj = subServiesArray.getJSONObject(k);
                                        //   Gson gson = new Gson();
                                        //  SubServices subServices = gson.fromJson(String.valueOf(jObj), SubServices.class);
                                        SubServices subServices = new SubServices();
                                        subServices._id = jObj.getString("_id");
                                        subServices.serviceId = jObj.getString("serviceId");
                                        subServices.subServiceId = jObj.getString("subServiceId");
                                        subServices.subServiceName = jObj.getString("subServiceName");

                                        JSONArray artistservices = jObj.getJSONArray("artistservices");
                                        for (int m=0; m<artistservices.length(); m++){
                                            JSONObject jsonObject3 = artistservices.getJSONObject(m);
                                            Gson gson2 = new Gson();
                                            ArtistServices services3 = gson2.fromJson(String.valueOf(jsonObject3), ArtistServices.class);
                                            services3.editedCtime = "";
                                            services3.editedOutCallP = "";
                                            services3.editedInCallP = "";
                                            services3.isInCallEdited = "";
                                            services3.isOutCallEdited = "";
                                            if (!services3.outCallPrice.equals("0") || !services3.outCallPrice.equals("null")){
                                                services3.isOutCall3 = true;
                                                subServices.isOutCall2 = true;
                                                services.isOutCall = true;
                                            }
                                            subServices.artistservices.add(services3);
                                        }

                                        services.arrayList.add(subServices);
                                    }
                                }
                                //  servicesList.add(services);
                                list.add(services);
                            }

                            if (companyDetail.staffService.size()!=0){
                                servicesList.clear();
                                serviceHashMap.clear();
                                for (ComapnySelectedServices artistServices2 : companyDetail.staffService){
                                    int artistServId = Integer.parseInt(artistServices2.serviceId);
                                    for (Services services : list) {
                                        if (artistServices2.serviceId.equals(services.serviceId))
                                        {
                                            for (SubServices subServices : services.arrayList) {
                                                if (artistServices2.subserviceId.equals(subServices.subServiceId)){
                                                    for (ArtistServices mainService : subServices.artistservices){
                                                        if (mainService._id.equals(artistServices2.artistServiceId)){
                                                            mainService.setSelected(true);
                                                            mainService._id = artistServices2._id;
                                                            mainService.completionTime = artistServices2.completionTime;
                                                            mainService.outCallPrice = artistServices2.outCallPrice;
                                                            mainService.inCallPrice = artistServices2.inCallPrice;
                                                            mainService.title = artistServices2.title;
                                                            serviceHashMap.put(artistServId,services);
                                                            //servicesList.add(services);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                for(Map.Entry<Integer, Services> entry : serviceHashMap.entrySet()){
                                    Services  services = entry.getValue();
                                    servicesList.add(services);
                                }

                            }else {
                                lvExpandable.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroyView() {
        ((CompanyServicesActivity)mContext).setTitle(getString(R.string.text_category));
        super.onDestroyView();
    }
}
