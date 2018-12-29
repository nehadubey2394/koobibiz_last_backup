package com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.profile_setup.BusinessProfileActivity;
import com.mualab.org.biz.modules.profile_setup.activity.AddNewServiceActivity;
import com.mualab.org.biz.modules.profile_setup.activity.EditServiceActivity;
import com.mualab.org.biz.modules.profile_setup.adapter.AddedServicesAdapter;
import com.mualab.org.biz.modules.profile_setup.db_modle.Services;
import com.mualab.org.biz.modules.profile_setup.fragment.ProfileCreationBaseFragment;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.modules.profile_setup.modle.FinalServiceForUpdateServer;
import com.mualab.org.biz.modules.profile_setup.modle.MyBusinessType;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class AddedServicesFragment extends ProfileCreationBaseFragment implements View.OnClickListener {

    private ProgressBar pbLoder;
    private AppCompatButton btnNext;
    private PreRegistrationSession bpSession;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private LinearLayout llAddService,llAddCategory;
    private String bizTypeId="",categoryId="";
    private View topLine;
    private List<MyBusinessType> bizypeList;
    private List<AddedCategory> categoryList;
    private List<Services> servicesList ;
    private Handler handler = new Handler(Looper.getMainLooper());
    private AddedServicesAdapter servicesListAdapter;
    private List<Services>tempServicesList;


    public void getCategoryAndBizTypeId(String bizTypeId,String categoryId){
        this.bizTypeId = bizTypeId;
        this.categoryId = categoryId;
        // aptForGetCategory();
    }

    public static AddedServicesFragment newInstance() {
        return new AddedServicesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        servicesList = new ArrayList<>();
        categoryList = new ArrayList<>();
        bizypeList = new ArrayList<>();
        tempServicesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_added_services, container, false);
        pbLoder = view.findViewById(R.id.pbLoder);
        btnNext = view.findViewById(R.id.btnNext);
        llAddService = view.findViewById(R.id.llAddService);
        llAddCategory = view.findViewById(R.id.llAddCategory);
        tvNoRecord = view.findViewById(R.id.tvNoRecord);
        topLine = view.findViewById(R.id.topLine);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        servicesListAdapter = new AddedServicesAdapter(mContext, tempServicesList,
                new AddedServicesAdapter.onClickListener() {
                    @Override
                    public void onEditClick(int pos) {
                        Intent intent = new Intent(mContext,EditServiceActivity.class);
                        intent.putExtra("serviceItem",tempServicesList.get(pos));
                        startActivityForResult(intent, Constant.EDIT_SERVICE);
                    }

                    @Override
                    public void onDelClick(int pos) {
                        deleteService(tempServicesList.get(pos));
                        tempServicesList.remove(pos);
                        servicesListAdapter.notifyItemRemoved(pos);
                    }
                });

        listViewServices = view.findViewById(R.id.listViewServices);

        // final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(servicesListAdapter);
        //listViewServices.addItemDecoration(headersDecor);

        // Add decoration for dividers between list items
        //    listViewServices.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));

        //  StickyRecyclerHeadersTouchListener touchListener = new StickyRecyclerHeadersTouchListener(listViewServices, headersDecor);
        //  listViewServices.addOnItemTouchListener(touchListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);
//        rycRecipeList.setHasFixedSize(true);
        listViewServices.setAdapter(servicesListAdapter);

        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddService.setOnClickListener(this);

        getAllServiceData();

    }

    private void deleteService(final Services services) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Mualab.get().getDB().businessTypeDao().deleteAll(services.bizypeList);
                // Mualab.get().getDB().categoryDao().deleteAll(services.categoryList);
                Mualab.get().getDB().serviceDao().delete(services);

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tempServicesList.size()!=0)
                            tvNoRecord.setVisibility(View.GONE);
                        else
                            tvNoRecord.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.btnNext:
                List<FinalServiceForUpdateServer>finalServiceForUpdateServers = new ArrayList<>();
                for (int i=0; i<servicesList.size();i++){
                    Services services = servicesList.get(i);
                    FinalServiceForUpdateServer forUpdateServer = new FinalServiceForUpdateServer();
                    forUpdateServer.serviceId = String.valueOf(services.serviceId);
                    forUpdateServer.subserviceId = String.valueOf(services.subserviceId);
                    forUpdateServer.description = services.description;
                    forUpdateServer.completionTime = services.completionTime;
                    forUpdateServer.title = services.serviceName;
                    forUpdateServer.outCallPrice = String.valueOf(services.outCallPrice);
                    forUpdateServer.inCallPrice = String.valueOf(services.inCallPrice);
                    finalServiceForUpdateServers.add(forUpdateServer);
                }

                Gson gson = new Gson();
                JsonArray jsonArray = gson.toJsonTree(finalServiceForUpdateServers).getAsJsonArray();
                if (!jsonArray.isJsonNull())
                    apiAddService(jsonArray);
                else
                    MyToast.getInstance(mContext).showDasuAlert("Please add service");


                break;
            case R.id.llAddCategory:
                BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                        (getActivity()) != null ?
                                (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                        activity.mPagerAdapter : null;

                if (adapter != null && viewPager!=null) {

                    if (listener!=null)
                        listener.onChangeByTag("Business Type");

                    MyBusinessTypeFragment fragment = (MyBusinessTypeFragment)
                            adapter.getRegisteredFragment(viewPager.getCurrentItem());

                    fragment.refreshData();

                }

                break;
            case R.id.llAddService:
                Intent intent = new Intent(mContext,AddNewServiceActivity.class);
                intent.putExtra("bizTypeId",bizTypeId);
                intent.putExtra("categoryId",categoryId);
                startActivityForResult(intent, Constant.REQUEST_NEW_SERVICE);
                break;
        }
    }

    private void getAllServiceData(){
        pbLoder.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                tempServicesList.clear();

                //   bizypeList = Mualab.get().getDB().businessTypeDao().getAll();
                // categoryList =  Mualab.get().getDB().categoryDao().getAll();
                servicesList =  Mualab.get().getDB().serviceDao().getAll();

                if (servicesList.size()!=0){
                    tempServicesList.addAll(servicesList);
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (tempServicesList.size()!=0) {
                            listViewServices.setVisibility(View.VISIBLE);
                            tvNoRecord.setVisibility(View.GONE);
                        }
                        else {
                            listViewServices.setVisibility(View.GONE);
                            tvNoRecord.setVisibility(View.VISIBLE);
                        }

                        pbLoder.setVisibility(View.GONE);

                        shortList();

                        if (tempServicesList.size()==0)
                            apiForGetService();

                    }
                });

            }
        }).start();


    }

    private void apiForGetService(){
        Progress.hide(mContext);
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetService();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "artistService",
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            JSONObject js = new JSONObject(response);
                            String status = js.getString("status");
                            String message = js.getString("message");

                            if (status.equalsIgnoreCase("success")) {
                                tvNoRecord.setVisibility(View.GONE);
                                listViewServices.setVisibility(View.VISIBLE);
                                topLine.setVisibility(View.VISIBLE);

                                JSONArray allServiceArray = js.getJSONArray("artistServices");

                                if (allServiceArray!=null) {
                                    for (int j=0; j<allServiceArray.length(); j++){
                                        JSONObject object = allServiceArray.getJSONObject(j);
                                        MyBusinessType businessType = new MyBusinessType();
                                        businessType.serviceId = Integer.parseInt(object.getString("serviceId"));
                                        businessType.serviceName = object.getString("serviceName");

                                        JSONArray subServiesArray = object.getJSONArray("subServies");
                                        if (subServiesArray!=null) {
                                            for (int k=0; k<subServiesArray.length(); k++){
                                                JSONObject jObj = subServiesArray.getJSONObject(k);
                                                AddedCategory subServices = new AddedCategory();
                                                subServices._id = Integer.parseInt(jObj.getString("_id"));
                                                subServices.serviceId = Integer.parseInt(jObj.getString("serviceId"));
                                                subServices.subServiceId = jObj.getString("subServiceId");
                                                subServices.subServiceName = jObj.getString("subServiceName");
                                                // services.categoryList.add(subServices);

                                                JSONArray artistservices = jObj.getJSONArray("artistservices");
                                                for (int m=0; m<artistservices.length(); m++){
                                                    Services services = new Services();
                                                    JSONObject jsonObject3 = artistservices.getJSONObject(m);
                                                    services.serviceId = businessType.serviceId;
                                                    services.bizTypeName = businessType.serviceName;
                                                    services.subserviceId = Integer.parseInt(subServices.subServiceId);
                                                    services.subserviceName = subServices.subServiceName;
                                                    services.id = Integer.parseInt(jsonObject3.getString("_id"));
                                                    services.serviceName = jsonObject3.getString("title");
                                                    services.completionTime = jsonObject3.getString("completionTime");
                                                    services.description = jsonObject3.getString("description");
                                                    services.inCallPrice = Double.parseDouble(jsonObject3.getString("inCallPrice"));
                                                    services.outCallPrice = Double.parseDouble(jsonObject3.getString("outCallPrice"));
                                                    tempServicesList.add(services);
                                                }
                                            }
                                        }
                                        // servicesList.add(services);
                                    }
                                }else {
                                    tvNoRecord.setVisibility(View.VISIBLE);
                                    listViewServices.setVisibility(View.GONE);
                                    topLine.setVisibility(View.GONE);
                                }

                            }else {
                                tvNoRecord.setVisibility(View.VISIBLE);
                                listViewServices.setVisibility(View.GONE);
                                topLine.setVisibility(View.GONE);
                            }
                            shortList();

                        } catch (Exception e) {
                            Progress.hide(mContext);
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorListener(VolleyError error) {
                        pbLoder.setVisibility(View.GONE);
                        try {
                            Helper helper = new Helper();
                            if (helper.error_Messages(error).contains("Session")) {
                                Mualab.getInstance().getSessionManager().logout();
                            }else
                                MyToast.getInstance(mContext).showDasuAlert(helper.error_Messages(error));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }})
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        task.execute(this.getClass().getName());
    }

    private void shortList() {
        Collections.sort(tempServicesList, new Comparator<Services>() {

            @Override
            public int compare(Services a1, Services a2) {

                if (a1.serviceId == 0 || a2.serviceId == 0)
                    return -1;
                else {
                    Long long1 = (long) a1.serviceId;
                    Long long2 = (long) a2.serviceId;
                    return long1.compareTo(long2);
                }
            }
        });
        servicesListAdapter.notifyDataSetChanged();

        if (tempServicesList.size()!=0) {
            listViewServices.setVisibility(View.VISIBLE);
            tvNoRecord.setVisibility(View.GONE);
        }
        else {
            listViewServices.setVisibility(View.GONE);
            tvNoRecord.setVisibility(View.VISIBLE);
        }

        //if (tempServicesList.size()==0)
        // apiForGetService();
    }

    /*update service data local db_modle to server db_modle*/

    private void apiAddService(final JsonArray jsonArray) {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddService(jsonArray);
                    }
                }
            }).show();
        }

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("artistService", String.valueOf(jsonArray));

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddService(jsonArray);
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext,
                "addArtistService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        Progress.hide(mContext);
                        // bpSession.updateRegStep(5);
                        listener.onChangeByTag("Upload Certification");

                    }else {
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    }
                    //    pbLoder.setVisibility(View.GONE);


                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                pbLoder.setVisibility(View.GONE);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                    }else
                        MyToast.getInstance(mContext).showDasuAlert(helper.error_Messages(error));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(body, HttpTask.ContentType.APPLICATION_JSON));

        task.execute(AddedServicesFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_NEW_SERVICE:
                    getAllServiceData();
                    break;

                case Constant.EDIT_SERVICE:
                    getAllServiceData();
                    break;
            }
        }

    }
}
