package com.mualab.org.biz.modules.profile_setup.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.profile_setup.adapter.AllBusinessTypeListAdapter;
import com.mualab.org.biz.modules.profile_setup.modle.AllBusinessType;
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

public class AddNewBusinessTypeActivity extends AppCompatActivity implements
        View.OnClickListener{
    private  String TAG = AddNewBusinessTypeActivity.class.getName();
    protected User user;
    private ProgressBar pbLoder;
    private AppCompatButton btnNext,btnAddNewBizType;
    private AllBusinessTypeListAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<AllBusinessType> allBusinessTypes;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private LinearLayout llNotRecord;
    private String searchText = "";
    private SearchView searchview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_business_type);
        init();
    }

    private void init(){
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(user==null) user = Mualab.getInstance().getSessionManager().getUser(); // get session object
        if(allBusinessTypes==null) allBusinessTypes = new ArrayList<>();

        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        tvHeaderText.setText("Add Business Type");
        ivKoobiLogo.setVisibility(View.GONE);
        tvHeaderText.setVisibility(View.VISIBLE);

        pbLoder = findViewById(R.id.pbLoder);
        btnNext = findViewById(R.id.btnNext);
        btnAddNewBizType = findViewById(R.id.btnAddNewBizType);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        llNotRecord = findViewById(R.id.llNotRecord);
        searchview = findViewById(R.id.searchview);
        listViewServices = findViewById(R.id.listViewServices);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(AddNewBusinessTypeActivity.this, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        // Create the adapter to convert the array to views
        adapter = new AllBusinessTypeListAdapter(AddNewBusinessTypeActivity.this,allBusinessTypes, new AllBusinessTypeListAdapter.BizTypeSelectListener() {
            @Override
            public void onBizTypeSelect(int position, AllBusinessType businessType) {
                for (AllBusinessType type : allBusinessTypes){
                    type.isChecked = false;
                }

                adapter.notifyDataSetChanged();
                allBusinessTypes.get(position).isChecked = !allBusinessTypes.get(position).isChecked;
                adapter.notifyItemChanged(position);
                if (allBusinessTypes.get(position).isChecked)
                    searchText = allBusinessTypes.get(position).title;
                else
                    searchText = "";
            }
        });

        listViewServices.setAdapter(adapter);

        KeyboardUtil.hideKeyboard(searchview, AddNewBusinessTypeActivity.this);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  filter(query);
                KeyboardUtil.hideKeyboard(searchview, AddNewBusinessTypeActivity.this);
                searchview.clearFocus();
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
                KeyboardUtil.hideKeyboard(searchview, AddNewBusinessTypeActivity.this);
                searchview.clearFocus();
                EditText et = searchview.findViewById(R.id.search_src_text);
                et.setText("");
                searchview.setQueryHint("Search...");
            }
        });

        iv_back.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnAddNewBizType.setOnClickListener(this);

        if(allBusinessTypes.size()==0){
            getAllBusinessType();
        }
    }

    private void filter(String newText){

        searchText = newText.trim();
        if (searchText.equals(""))
            KeyboardUtil.hideKeyboard(searchview, AddNewBusinessTypeActivity.this);

        getAllBusinessType();
    }

    private void getAllBusinessType() {

        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddNewBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        getAllBusinessType();
                    }
                }
            }).show();
        }

        String url = Uri.parse("allBusinessType")
                .buildUpon()
                .appendQueryParameter("search", searchText)
                .build().toString();

        HttpTask task = new HttpTask(new HttpTask.Builder(AddNewBusinessTypeActivity.this,
                url, new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    allBusinessTypes.clear();

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
                                AllBusinessType businessType = gson.fromJson(String.valueOf(object), AllBusinessType.class);
                                businessType.isChecked = false;
                                allBusinessTypes.add(businessType);
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
                            btnAddNewBizType.setVisibility(View.VISIBLE);
                        else
                            btnAddNewBizType.setVisibility(View.GONE);
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
                        MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert(helper.error_Messages(error));
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

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddNewBusinessTypeActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddBusinessType();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AddNewBusinessTypeActivity.this,
                "addBusinessType", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert("Business type added successfully");

                        EditText et = searchview.findViewById(R.id.search_src_text);
                        et.setText("");
                        searchview.setQueryHint("Search...");

                        Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        finish();

                    }else {
                        MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert(message);
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
                        MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert(helper.error_Messages(error));
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(3);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        KeyboardUtil.hideKeyboard(searchview, AddNewBusinessTypeActivity.this);
        PreRegistrationSession preSession = new PreRegistrationSession(this);
        preSession.updateRegStep(3);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 800) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (view.getId()){
            case R.id.btnNext:
                // updateDtataIntoServer();

                if (!searchText.equals(""))
                    apiAddBusinessType();
                else
                    MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert("Please select any business type.");

                break;

            case R.id.iv_back:
                finish();
                break;

            case R.id.btnAddNewBizType:
                if (!searchText.equals("")) {
                    if (searchText.length() <= 20)
                        apiAddBusinessType();
                    else
                        MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert("Maximum character limit should be 20.");

                } else
                    MyToast.getInstance(AddNewBusinessTypeActivity.this).showDasuAlert("Please select any business type.");
                break;

        }
    }

}
