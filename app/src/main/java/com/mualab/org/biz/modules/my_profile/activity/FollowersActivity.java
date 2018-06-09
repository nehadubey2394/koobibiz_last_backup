package com.mualab.org.biz.modules.my_profile.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.listner.EndlessRecyclerViewScrollListener;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.my_profile.adapter.FollowersAdapter;
import com.mualab.org.biz.modules.my_profile.model.Followers;
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

public class FollowersActivity extends AppCompatActivity {
    private RecyclerView rycFollowers;
    private  TextView tvNoData;
    private List<Followers>followers;
    private FollowersAdapter followersAdapter;
    private boolean isFollowers;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        initView();
    }

    private void initView(){
        Intent intent = getIntent();
        isFollowers = intent.getBooleanExtra("isFollowers",isFollowers);
        followers = new ArrayList<>();
        followersAdapter = new FollowersAdapter(FollowersActivity.this,followers,isFollowers);

        setView();
    }

    private void setView() {
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        if (isFollowers)
            tvHeaderTitle.setText(getString(R.string.text_follower));
        else
            tvHeaderTitle.setText(getString(R.string.text_following));
        tvNoData = findViewById(R.id.tvNoData);

        rycFollowers = findViewById(R.id.rycFollowers);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FollowersActivity.this, LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPositionWithOffset(0, 0);
        rycFollowers.setLayoutManager(layoutManager);
        rycFollowers.setAdapter(followersAdapter);

        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(scrollListener==null) {
            scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    followersAdapter.showLoading(true);
                    if (isFollowers)
                        apiForGetFollowers(page);
                    else
                        apiForGetFollowing(page);
                    //apiForLoadMoreArtist(page);
                }
            };
        }

        if(followers.size()==0) {
            if (isFollowers)
                apiForGetFollowers(0);
            else
                apiForGetFollowing(0);
        }
    }

    private void apiForGetFollowers(final int page){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(FollowersActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetFollowers(page);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(user.id));
        params.put("loginUserId", String.valueOf(user.id));
        params.put("page", String.valueOf(page));
        params.put("limit", "");

        HttpTask task = new HttpTask(new HttpTask.Builder(FollowersActivity.this, "followerList", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        followersAdapter.showLoading(false);

                        if (page==0) {
                            followers.clear();
                        }

                        rycFollowers.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);

                        JSONArray jsonArray = js.getJSONArray("followerList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                Followers item = gson.fromJson(String.valueOf(object), Followers.class);
                                followers.add(item);
                            }
                        }else {
                            rycFollowers.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        followersAdapter.notifyDataSetChanged();
                    }else {
                        rycFollowers.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(FollowersActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
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
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    private void apiForGetFollowing(final int page){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(FollowersActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetFollowing(page);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(user.id));
        params.put("loginUserId", String.valueOf(user.id));
        params.put("page", String.valueOf(page));
        params.put("limit", "");

        HttpTask task = new HttpTask(new HttpTask.Builder(FollowersActivity.this, "followingList", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        followersAdapter.showLoading(false);

                        if (page==0) {
                            followers.clear();
                        }

                        rycFollowers.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);

                        JSONArray jsonArray = js.getJSONArray("followingList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                Followers item = gson.fromJson(String.valueOf(object), Followers.class);
                                followers.add(item);
                            }
                        }else {
                            rycFollowers.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        followersAdapter.notifyDataSetChanged();
                    }else {
                        rycFollowers.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(FollowersActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
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
}
