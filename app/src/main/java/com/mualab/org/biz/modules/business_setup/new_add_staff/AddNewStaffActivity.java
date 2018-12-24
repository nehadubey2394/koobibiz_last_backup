package com.mualab.org.biz.modules.business_setup.new_add_staff;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.listner.EndlessRecyclerViewScrollListener;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AllArtist;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.business_setup.my_staff.adapter.MyStaffAdapter;
import com.mualab.org.biz.modules.business_setup.new_add_staff.adapter.AddStaffAdapter;
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

public class AddNewStaffActivity extends AppCompatActivity implements AddStaffAdapter.OnStaffClickListener {

    private  String TAG = AddNewStaffActivity.class.getName();
    private TextView tvNoDataFound;
    private List<AllArtist> artistStaffs;
    private AddStaffAdapter staffAdapter;
    private RecyclerView rvMyStaff;
    private   long mLastClickTime = 0;
    private SearchView searchview;
    private String searchText = "";
    private ProgressBar pbLoder;
    private EndlessRecyclerViewScrollListener scrollListener;

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
        staffAdapter = new AddStaffAdapter(AddNewStaffActivity.this, artistStaffs);
        pbLoder = findViewById(R.id.pbLoder);
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.add_staff));

        searchview = findViewById(R.id.searchview);

        rvMyStaff = findViewById(R.id.rvMyStaff);
        LinearLayoutManager layoutManager = new LinearLayoutManager(AddNewStaffActivity.this);
        rvMyStaff.setLayoutManager(layoutManager);
        rvMyStaff.setAdapter(staffAdapter);
        staffAdapter.setChangeListener(this);

        if(scrollListener==null) {
            scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount,
                                       RecyclerView view) {
                    staffAdapter.showLoading(true);
                    apiForGetAllStaff(page);
                    //apiForLoadMoreArtist(page);
                }
            };
        }

        //  rvMyStaff.setNestedScrollingEnabled(false);


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

        KeyboardUtil.hideKeyboard(searchview, AddNewStaffActivity.this);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //  filter(query);
                KeyboardUtil.hideKeyboard(searchview, AddNewStaffActivity.this);
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
                KeyboardUtil.hideKeyboard(searchview, AddNewStaffActivity.this);
                searchview.clearFocus();
                EditText et = searchview.findViewById(R.id.search_src_text);
                et.setText("");
                searchview.setQueryHint("Search...");
            }
        });
        apiForGetAllStaff(0);
    }

    private void filter(String newText){
        searchText = newText.trim();
        if (searchText.equals(""))
            KeyboardUtil.hideKeyboard(searchview, AddNewStaffActivity.this);

        apiForGetAllStaff(0);
    }

    private void apiForGetAllStaff(final int page){
        pbLoder.setVisibility(View.VISIBLE);
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(AddNewStaffActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetAllStaff(page);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("search", searchText);

        HttpTask task = new HttpTask(new HttpTask.Builder(AddNewStaffActivity.this, "allArtist", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    pbLoder.setVisibility(View.GONE);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        staffAdapter.showLoading(false);

                        if (page==0) {
                            artistStaffs.clear();
                        }

                        rvMyStaff.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.GONE);

                        JSONArray jsonArray = js.getJSONArray("artistList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                AllArtist item = gson.fromJson(String.valueOf(object), AllArtist.class);

                                artistStaffs.add(item);
                            }
                        }else if (artistStaffs.size()==0 && page==0){
                            rvMyStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }

                        staffAdapter.notifyDataSetChanged();
                    }else {
                        rvMyStaff.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    pbLoder.setVisibility(View.GONE);
                    Progress.hide(AddNewStaffActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                pbLoder.setVisibility(View.GONE);
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

    /*@Override
    public void onStaffDelete(int position, Staff staff) {
        showAlertDailog(position,staff);
    }*/

    private void showAlertDailog(final int position, final Staff staff){
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(AddNewStaffActivity.this, R.style.MyDialogTheme);
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
            new NoConnectionDialog(AddNewStaffActivity.this, new NoConnectionDialog.Listner() {
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

        HttpTask task = new HttpTask(new HttpTask.Builder(AddNewStaffActivity.this, "deleteStaff", new HttpResponceListner.Listener() {
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
                        MyToast.getInstance(AddNewStaffActivity.this).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(AddNewStaffActivity.this).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(AddNewStaffActivity.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStaffSelect(int position, StaffDetail item) {
        Intent intent = new Intent(AddNewStaffActivity.this, AddStaffDetailActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("staff",item);
        //args.putString("staffId", item.staffId);
        // args.putBoolean("isEdit", false);
        intent.putExtra("BUNDLE",args);
        startActivityForResult(intent,60);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==60 && resultCode!=0){
            apiForGetAllStaff(0);
        }
    }
}
