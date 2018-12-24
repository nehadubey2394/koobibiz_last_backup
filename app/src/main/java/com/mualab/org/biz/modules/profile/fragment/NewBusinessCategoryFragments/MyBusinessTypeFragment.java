package com.mualab.org.biz.modules.profile.fragment.NewBusinessCategoryFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.SubCategory;
import com.mualab.org.biz.model.serializer.SubCategorySerializer;
import com.mualab.org.biz.modules.profile.BusinessProfileActivity;
import com.mualab.org.biz.modules.profile.adapter.MyBusinessTypeListAdapter;
import com.mualab.org.biz.modules.profile.fragment.ProfileCreationBaseFragment;
import com.mualab.org.biz.modules.profile.listner.OnBusinessTypeSelectListner;
import com.mualab.org.biz.modules.profile.modle.MyBusinessType;
import com.mualab.org.biz.session.PreRegistrationSession;
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


public class MyBusinessTypeFragment extends ProfileCreationBaseFragment implements View.OnClickListener {

    private ProgressBar pbLoder;
    private AppCompatButton btnNext;
    private MyBusinessTypeListAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<MyBusinessType> allBusinessTypes;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private LinearLayout llAddBizType,llAddCategory;
    private String bizTypeId="";
    private View topLine;

    public void refreshData(){
        if (allBusinessTypes!=null && allBusinessTypes.size()!=0){
            for (MyBusinessType type : allBusinessTypes){
                type.isChecked = false;
            }

            adapter.notifyDataSetChanged();
            bizTypeId="";
            allBusinessTypes.clear();
        }

        getMyBusinessType();
    }

    public static MyBusinessTypeFragment newInstance() {
        return new MyBusinessTypeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(allBusinessTypes==null) allBusinessTypes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        pbLoder = view.findViewById(R.id.pbLoder);
        btnNext = view.findViewById(R.id.btnNext);
        llAddBizType = view.findViewById(R.id.llAddBizType);
        llAddCategory = view.findViewById(R.id.llAddCategory);
        tvNoRecord = view.findViewById(R.id.tvNoRecord);
        topLine = view.findViewById(R.id.topLine);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewServices = view.findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        // Create the adapter to convert the array to views
        adapter = new MyBusinessTypeListAdapter(mContext,allBusinessTypes, new MyBusinessTypeListAdapter.BizTypeSelectListener() {
            @Override
            public void onBizTypeSelect(int position, MyBusinessType businessType) {
                for (MyBusinessType type : allBusinessTypes){
                    type.isChecked = false;
                }

                adapter.notifyDataSetChanged();

                allBusinessTypes.get(position).isChecked = !allBusinessTypes.get(position).isChecked;
                adapter.notifyItemChanged(position);

                if (allBusinessTypes.get(position).isChecked)
                    bizTypeId = String.valueOf(allBusinessTypes.get(position).serviceId);
                else
                    bizTypeId = "";

            }

            @Override
            public void onBizTypeRemove(int position, MyBusinessType businessType) {


            }
        });
        listViewServices.setAdapter(adapter);

 /*       listViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                if(listener!=null && activity!=null){
                    ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                    PagerAdapter adapter = (getActivity()) != null ? activity.mPagerAdapter : null;
                    if (adapter != null && viewPager!=null) {
                        CategoriesFragmentCreation fragment = (CategoriesFragmentCreation) adapter.instantiateItem(null, viewPager.getCurrentItem()+1);

                        for(Category category: serviceList.get(position).categorys){
                            category.serviceName = serviceList.get(position).name;
                        }
                        fragment.updateView(serviceList.get(position).categorys);
                        //listener.saveTmpData("tmpService", serviceList.get(position));
                        listener.onNext();
                    }
                }
               *//* if((getActivity() != null ? ((BusinessProfileActivity) getActivity()).fragmentChangeListner : null) !=null){
                    BusinessProfileActivity.tmpData.put("tmpService", serviceList.get(position));
                    //((BusinessProfileActivity) getActivity()).fragmentChangeListner.onNext();
                    listener.saveTmpData("tmpService", serviceList.get(position));
                }*//*
            }
        });*/

        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddBizType.setOnClickListener(this);

        getMyBusinessType();

      /*  if(allBusinessTypes.size()>0){
            getAllSerVice();
        }else {
            getMyBusinessType();
        }*/
    }

    private void getMyBusinessType() {
        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        getMyBusinessType();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "myBusinessType", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    pbLoder.setVisibility(View.GONE);
                    allBusinessTypes.clear();

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    if (status.equalsIgnoreCase("success")) {
                        tvNoRecord.setVisibility(View.GONE);
                        listViewServices.setVisibility(View.VISIBLE);
                        topLine.setVisibility(View.VISIBLE);

                        JSONArray dataJsonArray = js.getJSONArray("data");
                        if (dataJsonArray != null && dataJsonArray.length() != 0) {
                            for (int j = 0; j < dataJsonArray.length(); j++) {
                                JSONObject object = dataJsonArray.getJSONObject(j);
                                Gson gson = new Gson();
                                MyBusinessType businessType = gson.fromJson(String.valueOf(object), MyBusinessType.class);
                                businessType.isChecked = false;
                                allBusinessTypes.add(businessType);
                            }
                        }

                    }else {
                        tvNoRecord.setVisibility(View.VISIBLE);
                        listViewServices.setVisibility(View.GONE);
                        topLine.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();

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
                .setProgress(false)
                .setMethod(Request.Method.GET)
                .setBodyContentType("application/x-www-form-urlencoded")
        );
        pbLoder.setVisibility(View.VISIBLE);
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private void populateServices(){
        adapter.notifyDataSetChanged();
        btnNext.setVisibility(BusinessProfileActivity.tmpSubCategory.size()>0?View.VISIBLE:View.GONE);
    }

     /*
    @SuppressLint("StaticFieldLeak")
    public void insertServices() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Mualab.get().getDB().serviceDao().deleteAll();
                Mualab.get().getDB().serviceDao().insertAll(serviceList);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void getAllSerVice() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                serviceList.clear();
                serviceList.addAll(Mualab.get().getDB().serviceDao().getAll());
                //margeServiceAndSubCategory(null);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                populateServices();
            }
        }.execute();
    }
*/

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.btnNext:

                //  updateDtataIntoServer();
               /* bpSession.updateRegStep(5);
                listener.onChangeByTag("Upload Certification");*/
                //   if (allBusinessTypes.size()==1)
                //      bizTypeId = allBusinessTypes.get(0).serviceId;


                if (allBusinessTypes.size()==0)
                    MyToast.getInstance(mContext).showDasuAlert("Please add business type.");
                else {
                    if (bizTypeId!=null && !bizTypeId.equals("") && !bizTypeId.isEmpty()){
                        BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                        BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                                (getActivity()) != null ?
                                        (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                                activity.mPagerAdapter : null;

                        if (adapter != null && viewPager!=null) {

                            listener.onChangeByTag("Service Category");

                            AddedCategoryFragment fragment = (AddedCategoryFragment)
                                    adapter.getRegisteredFragment(viewPager.getCurrentItem());

                            fragment.getCategoryId(bizTypeId);

                            bizTypeId = "";
                            //listener.saveTmpData("subCategory", subCategory);
                        }

                    }
                    else
                        MyToast.getInstance(mContext).showDasuAlert("Please select any business type.");

                }


                break;
            case R.id.llAddCategory:
                if (allBusinessTypes.size()==0)
                    MyToast.getInstance(mContext).showDasuAlert("Please add business type.");
                else {
                    if (bizTypeId!=null && !bizTypeId.equals("") && !bizTypeId.isEmpty()){
                        /*BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                        BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                                (getActivity()) != null ?
                                        (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                                activity.mPagerAdapter : null;

                        if (adapter != null && viewPager!=null) {

                            listener.onChangeByTag("Service Category");

                            AddedCategoryFragment fragment = (AddedCategoryFragment)
                                    adapter.getRegisteredFragment(viewPager.getCurrentItem());

                            fragment.getCategoryId(bizTypeId);

                            bizTypeId = "";
                            //listener.saveTmpData("subCategory", subCategory);
                        }*/
                        BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                        BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                                (getActivity()) != null ?
                                        (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                                activity.mPagerAdapter : null;

                        if (adapter != null && viewPager!=null) {

                            listener.onChangeByTag("Add New Service Category");

                            AddNewCategoryFragment fragment = (AddNewCategoryFragment)
                                    adapter.getRegisteredFragment(viewPager.getCurrentItem());

                            fragment.getCategoryId(bizTypeId);

                            bizTypeId = "";

                            //listener.saveTmpData("subCategory", subCategory);
                        }

                    }
                    else
                        MyToast.getInstance(mContext).showDasuAlert("Please select any business type.");

                }

                break;
            case R.id.llAddBizType:
                if (listener!=null)
                    listener.onNext();
               /* FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragContainer, new AddNewBusinessTypeActivity()
                        ,AddNewBusinessTypeActivity.class.getName());
                fragmentTransaction.commit();*/
                break;
        }
    }

    /*update service data local db_modle to server db_modle*/
    private void updateDtataIntoServer(){

        if(ConnectionDetector.isConnected()){
            Map<String,String> body = new HashMap<>();
            Gson gson = new GsonBuilder().registerTypeAdapter(SubCategory.class, new SubCategorySerializer()).create();
            body.put("artistService", gson.toJson(BusinessProfileActivity.tmpSubCategory));

            new HttpTask(new HttpTask.Builder(mContext, "addArtistService", new HttpResponceListner.Listener() {
                @Override
                public void onResponse(String response, String apiName) {
                    Log.d("res:", response);
                    Progress.hide(mContext);
                    bpSession.updateRegStep(5);
                    listener.onChangeByTag("Upload Certification");
                    //insertServices(addMoreService);
                }

                @Override
                public void ErrorListener(VolleyError error) {
                    Progress.hide(mContext);
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
                    .setMethod(Request.Method.POST)
                    .setParam(body)
                    .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                    .setBody(body)
                    .setProgress(true)
                    .setAuthToken(user.authToken)).execute("updateRange");
        }else showToast(R.string.error_msg_network);
    }
}
