package com.mualab.org.biz.modules.profile_setup.fragment.NewBusinessCategoryFragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.mualab.org.biz.modules.profile_setup.adapter.AllCategoryListAdapter;
import com.mualab.org.biz.modules.profile_setup.fragment.ProfileCreationBaseFragment;
import com.mualab.org.biz.modules.profile_setup.modle.AllCategory;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.KeyboardUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddNewCategoryFragment extends ProfileCreationBaseFragment implements View.OnClickListener {
    private  String TAG = AddNewCategoryFragment.class.getName();
    private ProgressBar pbLoder;
    private AppCompatButton btnContinue,btnAddNewCategory;
    private AllCategoryListAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<AllCategory> allCategoryList;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private LinearLayout llNotRecord;
    private String searchText = "",bizTypeId = "";
    private SearchView searchview;

    public void getCategoryId(String bizTypeId){
        this.bizTypeId = bizTypeId;
        getAllCategory();
    }


    public static AddNewCategoryFragment newInstance() {
        return new AddNewCategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(allCategoryList==null) allCategoryList = new ArrayList<>();
        //if(actualServiceList==null) actualServiceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_new_category, container, false);
        pbLoder = view.findViewById(R.id.pbLoder);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnAddNewCategory = view.findViewById(R.id.btnAddNewCategory);
        tvNoRecord = view.findViewById(R.id.tvNoRecord);
        llNotRecord = view.findViewById(R.id.llNotRecord);
        searchview = view.findViewById(R.id.searchview);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewServices = view.findViewById(R.id.listViewServices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        // Create the adapter to convert the array to views
        adapter = new AllCategoryListAdapter(mContext,allCategoryList,
                new AllCategoryListAdapter.CategorySelectListener() {
                    @Override
                    public void onCategorySelect(int position, AllCategory businessType) {
                        for (AllCategory type : allCategoryList){
                            type.isChecked = false;
                        }

                        adapter.notifyDataSetChanged();
                        allCategoryList.get(position).isChecked = !allCategoryList.get(position).isChecked;
                        adapter.notifyItemChanged(position);
                        if (allCategoryList.get(position).isChecked)
                            searchText = allCategoryList.get(position).title;
                        else
                            searchText = "";
                    }
                });

        listViewServices.setAdapter(adapter);

        KeyboardUtil.hideKeyboard(searchview, mContext);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  filter(query);
                KeyboardUtil.hideKeyboard(searchview, mContext);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Mualab.getInstance().cancelPendingRequests(TAG);
                filter(newText);
                return true;
            }
        });

        searchview.findViewById(R.id.search_close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideKeyboard(searchview, mContext);
                searchview.clearFocus();
                EditText et = searchview.findViewById(R.id.search_src_text);
                et.setText("");
                searchview.setQueryHint("Search...");
            }
        });

        btnContinue.setOnClickListener(this);
        btnAddNewCategory.setOnClickListener(this);

        /*if(allCategoryList.size()==0){
            getAllCategory();
        }*/
    }

    private void filter(String newText){
        searchText = newText.trim();
        if (searchText.equals(""))
            KeyboardUtil.hideKeyboard(searchview, mContext);

        getAllCategory();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.btnContinue:
                // updateDtataIntoServer();

                if (!searchText.equals(""))
                    apiAddBusinessType();
                else
                    MyToast.getInstance(mContext).showDasuAlert("Please select any category.");

                break;

            case R.id.btnAddNewCategory:
                if (!searchText.equals(""))
                    apiAddBusinessType();
                else
                    MyToast.getInstance(mContext).showDasuAlert("Please select any category.");
                break;

        }
    }

    private void getAllCategory() {

        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        getAllCategory();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext,
                "allSubcategory?search="+searchText+"&categoryId=" +bizTypeId
                , new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    allCategoryList.clear();

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        llNotRecord.setVisibility(View.GONE);
                        listViewServices.setVisibility(View.VISIBLE);

                        JSONArray dataJsonArray = js.getJSONArray("data");
                        if (dataJsonArray != null && dataJsonArray.length() != 0) {
                            for (int j = 0; j < dataJsonArray.length(); j++) {
                                JSONObject object = dataJsonArray.getJSONObject(j);
                                Gson gson = new Gson();
                                AllCategory allCategory = gson.fromJson(String.valueOf(object), AllCategory.class);
                                allCategory.isChecked = false;
                                allCategoryList.add(allCategory);
                            }
                        }
                      /*  Gson gson = new Gson();
                        ParseService tmp = gson.fromJson(response, ParseService.class);
                        serviceList.addAll(tmp.serviceList);
                        //margeServiceAndSubCategory(tmp.subCategory);
                        insertServices();
                        tmp.serviceList.clear();
                        tmp = null;
                        populateServices();*/
                    }else {
                        llNotRecord.setVisibility(View.VISIBLE);
                        listViewServices.setVisibility(View.GONE);
                        if (!searchText.equals(""))
                            btnAddNewCategory.setVisibility(View.VISIBLE);
                        else
                            btnAddNewCategory.setVisibility(View.GONE);

                    }
                    pbLoder.setVisibility(View.GONE);
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

        task.execute(TAG);
    }

    private void apiAddBusinessType() {

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("title",searchText);
        body.put("categoryId",bizTypeId);

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddBusinessType();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext,
                "addSubCategory", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        EditText et = searchview.findViewById(R.id.search_src_text);
                        et.setText("");
                        searchview.setQueryHint("Search...");

                        BusinessProfileActivity activity = ((BusinessProfileActivity) getActivity());
                        ViewPager viewPager = (getActivity()) != null ? activity.mPager : null;
                        BusinessProfileActivity.ScreenSlidePagerAdapter adapter =
                                (getActivity()) != null ?
                                        (BusinessProfileActivity.ScreenSlidePagerAdapter)
                                                activity.mPagerAdapter : null;

                        if (adapter != null && viewPager!=null) {

                            if (listener!=null)
                                listener.onPrev();

                            AddedCategoryFragment fragment = (AddedCategoryFragment)
                                    adapter.getRegisteredFragment(viewPager.getCurrentItem());

                            fragment.getCategoryId(bizTypeId);

                            //listener.saveTmpData("subCategory", subCategory);
                        }



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

        task.execute(TAG);
    }


/*
    private void margeServiceAndSubCategory(List<SubCategory> serverSubCategory){
        BusinessProfileActivity.tmpSubCategory.clear();
        //BusinessProfileActivity.tmpSubCategory = serverSubCategory!=null?serverSubCategory:bpSession.getBusinessProfile().subCategories;

        if(serverSubCategory!=null)
            BusinessProfileActivity.tmpSubCategory.addAll(serverSubCategory);
        // else BusinessProfileActivity.tmpSubCategory = bpSession.getBusinessProfile().subCategories;

        for(SubCategory subCategory : BusinessProfileActivity.tmpSubCategory){

            for(Service service : allCategoryList){

                if(service.id == subCategory.serviceId ){
                    Category category = service.getCategory(subCategory.categoryId);
                    if(category!=null){
                        category.serviceName = service.name;
                        category.addSubCategory(subCategory);
                    }
                }
            }
        }
    }
*/

    private void populateServices(){
        adapter.notifyDataSetChanged();
        btnContinue.setVisibility(BusinessProfileActivity.tmpSubCategory.size()>0?View.VISIBLE:View.GONE);
    }

    /*update service data local db_modle to server db_modle*/
    private void updateDtataIntoServer()
    {

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
