package com.mualab.org.biz.modules.business_setup.my_staff;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AddedStaffServices;
import com.mualab.org.biz.model.add_staff.BusinessDayForStaff;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.add_staff.fragments.ArtistStaffFragment;
import com.mualab.org.biz.modules.business_setup.my_staff.adapter.MyStaffAdapter;
import com.mualab.org.biz.modules.business_setup.new_add_staff.AddStaffDetailActivity;
import com.mualab.org.biz.session.Session;
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

import views.refreshview.CircleHeaderView;
import views.refreshview.OnRefreshListener;
import views.refreshview.RjRefreshLayout;

public class MyStaffActivity extends AppCompatActivity implements
        MyStaffAdapter.OnDeleteStaffListener {

    private  String TAG = MyStaffActivity.class.getName();
    private TextView tvNoDataFound;
    private List<Staff> artistStaffs;
    private MyStaffAdapter staffAdapter;
    private RecyclerView rvMyStaff;
    private   long mLastClickTime = 0;
    private SearchView searchview;
    private String searchText = "";
    private ProgressBar pbLoder;
    private RjRefreshLayout mRefreshLayout;
    private boolean isPulltoRefrash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_staff);
        initView();
    }

    private void initView(){
        // if(mContext instanceof AddNewStaffActivity) {
        //   ((AddNewStaffActivity) mContext).setTitle();
        artistStaffs = new ArrayList<>();
        staffAdapter = new MyStaffAdapter(MyStaffActivity.this, artistStaffs);
        pbLoder = findViewById(R.id.pbLoder);
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.text_staff));

        searchview = findViewById(R.id.searchview);
        mRefreshLayout =  findViewById(R.id.mSwipeRefreshLayout);
        rvMyStaff = findViewById(R.id.rvMyStaff);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyStaffActivity.this);
        rvMyStaff.setLayoutManager(layoutManager);
        //  rvMyStaff.setNestedScrollingEnabled(false);
        rvMyStaff.setAdapter(staffAdapter);
        staffAdapter.setChangeListener(this);
        staffAdapter.setChangeListener(this);

        final CircleHeaderView header = new CircleHeaderView(MyStaffActivity.this);

        mRefreshLayout.addHeader(header);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPulltoRefrash = true;
                apiForGetArtistStaff();
            }

            @Override
            public void onLoadMore() {
                Log.e(TAG, "onLoadMore: ");
            }
        });

        tvNoDataFound = findViewById(R.id.tvNoDataFound);

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                finish();
            }
        });

        KeyboardUtil.hideKeyboard(searchview, MyStaffActivity.this);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  filter(query);
                KeyboardUtil.hideKeyboard(searchview, MyStaffActivity.this);
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
                KeyboardUtil.hideKeyboard(searchview, MyStaffActivity.this);
                searchview.clearFocus();
                EditText et = searchview.findViewById(R.id.search_src_text);
                et.setText("");
                searchview.setQueryHint("Search...");
            }
        });
        apiForGetArtistStaff();
    }

    private void filter(String newText){
        searchText = newText.trim();
        if (searchText.equals(""))
            KeyboardUtil.hideKeyboard(searchview, MyStaffActivity.this);

        apiForGetArtistStaff();
    }

    private void apiForGetArtistStaff(){
        if(isPulltoRefrash)
            pbLoder.setVisibility(View.GONE);
        else
            pbLoder.setVisibility(View.VISIBLE);

        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyStaffActivity.this, new NoConnectionDialog.Listner() {
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
        params.put("search", searchText);

        HttpTask task = new HttpTask(new HttpTask.Builder(MyStaffActivity.this, "artistStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    pbLoder.setVisibility(View.GONE);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        rvMyStaff.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.GONE);

                        artistStaffs.clear();

                        if(isPulltoRefrash){
                            isPulltoRefrash = false;
                            mRefreshLayout.stopRefresh(true, 500);
                            int prevSize = artistStaffs.size();
                            staffAdapter.notifyItemRangeRemoved(0, prevSize);
                        }

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
                            rvMyStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }

                        staffAdapter.notifyDataSetChanged();

                        if (artistStaffs.size() == 0) {
                            rvMyStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                            if(isPulltoRefrash){
                                isPulltoRefrash = false;
                                mRefreshLayout.stopRefresh(false, 500);

                            }
                        }

                    }else {
                        rvMyStaff.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    pbLoder.setVisibility(View.GONE);
                    Progress.hide(MyStaffActivity.this);
                    e.printStackTrace();

                    staffAdapter.notifyDataSetChanged();

                    if (artistStaffs.size() == 0) {
                        rvMyStaff.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                        if(isPulltoRefrash){
                            isPulltoRefrash = false;
                            mRefreshLayout.stopRefresh(false, 500);

                        }
                    }
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                pbLoder.setVisibility(View.GONE);
                if(isPulltoRefrash){
                    isPulltoRefrash = false;
                    mRefreshLayout.stopRefresh(false, 500);
                    int prevSize = artistStaffs.size();
                    artistStaffs.clear();
                    staffAdapter.notifyItemRangeRemoved(0, prevSize);
                }
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
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(TAG);
    }

    @Override
    public void onStaffDelete(int position, Staff staff) {
        showAlertDailog(position,staff);
    }

    @Override
    public void onStaffSelect(int position, Staff staff) {
        apiForGetStaffDetail(staff);
    }

    private void showAlertDailog(final int position, final Staff staff){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(MyStaffActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage("Are you sure want to remove this staff?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                dialog.cancel();
                apiForDeleteStaff(staff);

            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();

    }

    private void apiForDeleteStaff(final Staff staff){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyStaffActivity.this, new NoConnectionDialog.Listner() {
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

        HttpTask task = new HttpTask(new HttpTask.Builder(MyStaffActivity.this, "deleteStaff", new HttpResponceListner.Listener() {
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
                            rvMyStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                        MyToast.getInstance(MyStaffActivity.this).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(MyStaffActivity.this).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(MyStaffActivity.this);
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

    private void apiForGetStaffDetail(final Staff staff){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyStaffActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetStaffDetail(staff);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staff.staffId);
        params.put("businessId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(MyStaffActivity.this, "staffInformation", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        JSONArray jsonArray = js.getJSONArray("staffDetails");

                        if (jsonArray!=null && jsonArray.length()!=0) {
                            StaffDetail item = null;
                            for (int i=0; i<jsonArray.length(); i++){

                                item = new StaffDetail();
                                JSONObject object = jsonArray.getJSONObject(i);

                                item._id = object.getString("_id");
                                item.job = object.getString("job");
                                item.mediaAccess = object.getString("mediaAccess");
                                item.holiday = object.getString("holiday");

                                JSONObject staffInfo = object.getJSONObject("staffInfo");
                                item.userName = staffInfo.getString("userName");
                                item.profileImage = staffInfo.getString("profileImage");
                                item.staffId = staffInfo.getString("staffId");

                                JSONArray staffHoursArray = object.getJSONArray("staffHours");
                                for (int j=0; j<staffHoursArray.length(); j++){
                                    JSONObject object2 = staffHoursArray.getJSONObject(j);
                                    BusinessDayForStaff item2 = new BusinessDayForStaff();
                                    // Gson gson = new Gson();
                                    // BusinessDayForStaff item2 = gson.fromJson(String.valueOf(object2), BusinessDayForStaff.class);
                                    if (object2.has("day"))
                                        item2.day = Integer.parseInt(object2.getString("day"));
                                    if (object2.has("dayId"))
                                        item2.day = Integer.parseInt(object2.getString("dayId"));

                                    item2.endTime = object2.getString("endTime");
                                    item2.startTime = object2.getString("startTime");
                                    item.staffHoursList.add(item2);
                                }

                                JSONArray staffServiceArray = object.getJSONArray("staffService");
                                for (int k=0; k<staffServiceArray.length(); k++){
                                    JSONObject object3 = staffServiceArray.getJSONObject(k);
                                    Gson gson = new Gson();
                                    AddedStaffServices item3 = gson.fromJson(String.valueOf(object3), AddedStaffServices.class);
                                    if (!item3.inCallPrice.equals("0.0"))
                                        item3.bookingType = "Incall";
                                    else if (!item3.outCallPrice.equals("0.0"))
                                        item3.bookingType = "Outcall";
                                    else if (!item3.inCallPrice.equals("0.0") && !item3.outCallPrice.equals("0.0"))
                                        item3.bookingType = "Both";

                                    item.staffServices.add(item3);
                                }
                            }
                            if (item!=null) {
                                Intent intent = new Intent(MyStaffActivity.this, AddStaffDetailActivity.class);
                                Bundle args = new Bundle();
                                args.putSerializable("staff", item);
                                intent.putExtra("BUNDLE", args);
                                startActivityForResult(intent,60);
                                // ((AddNewStaffActivity) context).finish();
                            }
                        }

                    }else {
                        MyToast.getInstance(MyStaffActivity.this).showDasuAlert(message);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(MyStaffActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==60 && resultCode!=0){
            searchview.clearFocus();
            KeyboardUtil.hideKeyboard(searchview, MyStaffActivity.this);
            EditText et = searchview.findViewById(R.id.search_src_text);
            et.setText("");

            apiForGetArtistStaff();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
