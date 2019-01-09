package com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.mualab.org.biz.modules.profile_setup.BusinessProfileActivity;
import com.mualab.org.biz.modules.profile_setup.activity.AddNewCategoryActivity;
import com.mualab.org.biz.modules.profile_setup.activity.AddNewServiceActivity;
import com.mualab.org.biz.modules.profile_setup.adapter.AddedCategoryListAdapter;
import com.mualab.org.biz.modules.profile_setup.fragment.ProfileCreationBaseFragment;
import com.mualab.org.biz.modules.profile_setup.modle.AddedCategory;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class AddedCategoryFragment extends ProfileCreationBaseFragment implements View.OnClickListener {

    private ProgressBar pbLoder;
    private AppCompatButton btnNext;
    private AddedCategoryListAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<AddedCategory> addedCategoryList;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private LinearLayout llAddService,llAddCategory;
    private String bizTypeId="",categoryId="";
    private View topLine;

    public void getCategoryId(String bizTypeId){
        if (addedCategoryList!=null && addedCategoryList.size()!=0){
            for (AddedCategory type : addedCategoryList){
                type.isChecked = false;
            }

            adapter.notifyDataSetChanged();
            categoryId = "";
            addedCategoryList.clear();
        }

        this.bizTypeId = bizTypeId;
        apiForGetCategory();
    }

    public static AddedCategoryFragment newInstance() {
        return new AddedCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(addedCategoryList==null) addedCategoryList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_added_category, container, false);
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
        listViewServices = view.findViewById(R.id.listViewServices);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        // Create the adapter to convert the array to views
        adapter = new AddedCategoryListAdapter(mContext, addedCategoryList, new AddedCategoryListAdapter.CategorySelectListener() {
            @Override
            public void onBizTypeSelect(int position, AddedCategory addedCategory) {
                for (AddedCategory type : addedCategoryList){
                    type.isChecked = false;
                }

                adapter.notifyDataSetChanged();

                addedCategoryList.get(position).isChecked = !addedCategoryList.get(position).isChecked;
                adapter.notifyItemChanged(position);

                if (addedCategoryList.get(position).isChecked)
                    categoryId = String.valueOf(addedCategoryList.get(position)._id);
                else
                    categoryId = "";
            }

            @Override
            public void onCategoryRemove(int position, AddedCategory addedCategory) {


            }

        });
        listViewServices.setAdapter(adapter);

        btnNext.setOnClickListener(this);
        llAddCategory.setOnClickListener(this);
        llAddService.setOnClickListener(this);

        //  apiForGetCategory();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                apiForGetCategory();
            }
        },1000);*/


    }

    private void apiForGetCategory() {

        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForGetCategory();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext,
                "mysubCategory?categoryId="+bizTypeId,
                new HttpResponceListner.Listener() {
                    @Override
                    public void onResponse(String response, String apiName) {
                        try {
                            pbLoder.setVisibility(View.GONE);
                            addedCategoryList.clear();

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
                                        AddedCategory addedCategory = gson.fromJson(String.valueOf(object), AddedCategory.class);
                                        addedCategory.isChecked = false;
                                        addedCategoryList.add(addedCategory);
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

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
        BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                (getActivity()) != null ?
                        (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                activity.mPagerAdapter : null;


        switch (view.getId()){
            case R.id.btnNext:
                if (!categoryId.equals("")){
                    if (adapter != null && viewPager!=null) {

                        listener.onChangeByTag("company_services");

                        categoryId = "";

                    }
                } else
                    MyToast.getInstance(mContext).showDasuAlert("Please Select any category");



                break;
            case R.id.llAddCategory:
                if (!bizTypeId.equals("")){

                    Intent intent = new Intent(mContext,AddNewCategoryActivity.class);
                    intent.putExtra("bizTypeId",bizTypeId);
                    startActivityForResult(intent, Constant.REQUEST_NEW_CATEGORY);

                  /*  if (adapter != null && viewPager!=null) {

                        listener.onChangeByTag("Service Category");

                        AddNewCategoryFragment fragment = (AddNewCategoryFragment)
                                adapter.getRegisteredFragment(5);

                        fragment.getCategoryId(bizTypeId);

                        //listener.saveTmpData("subCategory", subCategory);
                    }*/

                }
                else
                    MyToast.getInstance(mContext).showDasuAlert("Please Select one Business Type");

                break;
            case R.id.llAddService:

                if (!categoryId.equals("")){
                    Intent intent = new Intent(mContext,AddNewServiceActivity.class);
                    intent.putExtra("bizTypeId",bizTypeId);
                    intent.putExtra("categoryId",categoryId);
                    startActivityForResult(intent, Constant.REQUEST_NEW_SERVICE);

                   /* if (adapter != null && viewPager!=null) {

                        listener.onChangeByTag("company_services");

                        AddedServicesFragment fragment = (AddedServicesFragment)
                                adapter.getRegisteredFragment(viewPager.getCurrentItem());

                        fragment.getCategoryAndBizTypeId(bizTypeId,categoryId);
                        //listener.saveTmpData("subCategory", subCategory);
                    }*/
                }else
                    MyToast.getInstance(mContext).showDasuAlert("Please select any category.");

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constant.REQUEST_NEW_CATEGORY:
                    if (addedCategoryList!=null && addedCategoryList.size()!=0){
                        for (AddedCategory type : addedCategoryList){
                            type.isChecked = false;
                        }

                        adapter.notifyDataSetChanged();
                        categoryId = "";
                        addedCategoryList.clear();
                    }
                    apiForGetCategory();
                    break;

                case Constant.REQUEST_NEW_SERVICE:
                    BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                    ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                    BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                            (getActivity()) != null ?
                                    (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                            activity.mPagerAdapter : null;

                    if (adapter != null && viewPager!=null) {

                        listener.onChangeByTag("company_services");

                        AddedServicesFragment fragment = (AddedServicesFragment)
                                adapter.getRegisteredFragment(viewPager.getCurrentItem());

                        fragment.getCategoryAndBizTypeId(bizTypeId,categoryId);
                        //listener.saveTmpData("subCategory", subCategory);
                    }
                    break;
            }
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
