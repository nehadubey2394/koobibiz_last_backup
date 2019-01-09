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
import com.mualab.org.biz.modules.profile_setup.adapter.AllCategoryListAdapter;
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

public class AddNewCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    private  String TAG = AddNewCategoryActivity.class.getName();
    private ProgressBar pbLoder;
    private AppCompatButton btnAddNewCategory;
    private AllCategoryListAdapter adapter;
    private PreRegistrationSession bpSession;
    private List<AllCategory> allCategoryList;
    private TextView tvNoRecord;
    private long mLastClickTime = 0;
    private RecyclerView listViewServices;
    private LinearLayout llNotRecord;
    private String searchText = "",bizTypeId = "",commingFrom;
    private SearchView searchview;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);
        bizTypeId = getIntent().getStringExtra("bizTypeId");
        commingFrom = getIntent().getStringExtra("commingFrom");
        bpSession = Mualab.getInstance().getBusinessProfileSession();
        if(allCategoryList==null) allCategoryList = new ArrayList<>();
        init();
    }

    private void init(){
        user = Mualab.getInstance().getSessionManager().getUser();
        pbLoder = findViewById(R.id.pbLoder);
        AppCompatButton btnContinue = findViewById(R.id.btnContinue);
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory);
        tvNoRecord = findViewById(R.id.tvNoRecord);
        llNotRecord = findViewById(R.id.llNotRecord);
        searchview = findViewById(R.id.searchview);
        listViewServices = findViewById(R.id.listViewServices);

        ImageView iv_back = findViewById(R.id.iv_back);
        ImageView ivKoobiLogo = findViewById(R.id.ivKoobiLogo);
        TextView tvHeaderText = findViewById(R.id.tvHeaderText);
        tvHeaderText.setText(getString(R.string.add_category));
        ivKoobiLogo.setVisibility(View.GONE);
        tvHeaderText.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(AddNewCategoryActivity.this, LinearLayoutManager.VERTICAL, false);
        listViewServices.setLayoutManager(layoutManager);

        adapter = new AllCategoryListAdapter(AddNewCategoryActivity.this,allCategoryList,
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

        KeyboardUtil.hideKeyboard(searchview, AddNewCategoryActivity.this);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  filter(query);
                KeyboardUtil.hideKeyboard(searchview, AddNewCategoryActivity.this);
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
                KeyboardUtil.hideKeyboard(searchview, AddNewCategoryActivity.this);
                searchview.clearFocus();
                EditText et = searchview.findViewById(R.id.search_src_text);
                et.setText("");
                searchview.setQueryHint("Search...");
            }
        });

        btnContinue.setOnClickListener(this);
        btnAddNewCategory.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        getAllCategory();
    }

    private void filter(String newText){
        searchText = newText.trim();
        if (searchText.equals(""))
            KeyboardUtil.hideKeyboard(searchview, AddNewCategoryActivity.this);

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
                    apiAddCategory();
                else
                    MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert("Please select any category.");

                break;

            case R.id.btnAddNewCategory:
                if (!searchText.equals(""))
                    if (searchText.length() <= 20)
                        apiAddCategory();
                    else
                        MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert("Maximum character limit should be 20.");

                else
                    MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert("Please select any category.");
                break;

            case R.id.iv_back:
                finish();
                break;


        }
    }

    private void getAllCategory() {

        pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddNewCategoryActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        getAllCategory();
                    }
                }
            }).show();
        }

        String url = Uri.parse("allSubcategory")
                .buildUpon()
                .appendQueryParameter("search", searchText)
                .appendQueryParameter("categoryId", bizTypeId)
                .build().toString();

        HttpTask task = new HttpTask(new HttpTask.Builder(AddNewCategoryActivity.this,
                url, new HttpResponceListner.Listener() {
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
                        MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert(helper.error_Messages(error));
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

    private void apiAddCategory() {

        //  pbLoder.setVisibility(View.VISIBLE);
        Map<String,String> body = new HashMap<>();
        body.put("title",searchText);
        body.put("categoryId",bizTypeId);

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddNewCategoryActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiAddCategory();
                    }
                }
            }).show();
        }

        HttpTask task = new HttpTask(new HttpTask.Builder(AddNewCategoryActivity.this,
                "addSubCategory", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert("Category added successfully");

                        EditText et = searchview.findViewById(R.id.search_src_text);
                        et.setText("");
                        searchview.setQueryHint("Search...");

                       /* Intent intent = getIntent();
                        setResult(RESULT_OK, intent);
                        finish();*/
                        Intent intent = new Intent(AddNewCategoryActivity.this,MyAddedCategoryTypeActivity.class);
                        intent.putExtra("bizTypeId",bizTypeId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }else {
                        MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert(message);
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
                        MyToast.getInstance(AddNewCategoryActivity.this).showDasuAlert(helper.error_Messages(error));
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
    protected void onDestroy() {
        super.onDestroy();
        KeyboardUtil.hideKeyboard(searchview, AddNewCategoryActivity.this);

    }
}
