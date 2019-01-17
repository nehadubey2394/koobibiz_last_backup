package com.mualab.org.biz.modules.new_my_profile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.listner.RecyclerViewScrollListener;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.my_profile.activity.CommentsActivity;
import com.mualab.org.biz.modules.my_profile.activity.FollowersActivity;
import com.mualab.org.biz.modules.my_profile.adapter.feeds.FeedAdapter;
import com.mualab.org.biz.modules.my_profile.model.Feeds;
import com.mualab.org.biz.modules.my_profile.model.UserProfileData;
import com.mualab.org.biz.modules.new_my_profile.adapter.NavigationMenuAdapter;
import com.mualab.org.biz.modules.new_my_profile.model.NavigationItem;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import views.refreshview.RjRefreshLayout;

public class MyProfileActivity extends AppCompatActivity implements View.OnClickListener,FeedAdapter.Listener,NavigationMenuAdapter.Listener {
    private TextView tvImages,tvVideos,tvFeeds,tv_msg,tv_no_data_msg,tv_dot1,tv_dot2;
    private ImageView iv_profile_back ,iv_profile_forward,ivActive;
    private LinearLayout lowerLayout1,lowerLayout2,ll_progress;
    private RecyclerView rvFeed;
    private RjRefreshLayout mRefreshLayout;
    private RecyclerViewScrollListener endlesScrollListener;
    private int CURRENT_FEED_STATE = 0,lastFeedTypeId;
    private String feedType = "";
    private FeedAdapter feedAdapter;
    private List<Feeds> feeds;
    private boolean isPulltoRefrash = false;
    private  long mLastClickTime = 0;
    private DrawerLayout drawer;
    private List<NavigationItem> navigationItems;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        initView();

    }


    private void initView(){
        user = Mualab.getInstance().getSessionManager().getUser();
        feeds = new ArrayList<>();
        navigationItems = new ArrayList<>();
        tvImages = findViewById(R.id.tv_image);
        tvVideos = findViewById(R.id.tv_videos);
        tvFeeds =  findViewById(R.id.tv_feed);
        tv_dot1 =  findViewById(R.id.tv_dot1);
        tv_dot2 =  findViewById(R.id.tv_dot2);
        rvFeed =  findViewById(R.id.rvFeed);

        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(getString(R.string.profile));

        LinearLayout lyImage = findViewById(R.id.ly_images);
        LinearLayout lyVideos = findViewById(R.id.ly_videos);
        LinearLayout lyFeed = findViewById(R.id.ly_feeds);

        LinearLayout llServices = findViewById(R.id.llServices);
        LinearLayout llCertificate = findViewById(R.id.llCertificate);
        LinearLayout llAboutUs = findViewById(R.id.llAboutUs);
        LinearLayout llFollowers = findViewById(R.id.llFollowers);
        LinearLayout llFollowing = findViewById(R.id.llFollowing);
        LinearLayout llPost = findViewById(R.id.llPost);

        lowerLayout2 =  findViewById(R.id.lowerLayout2);
        lowerLayout1 =  findViewById(R.id.lowerLayout);

        //  ImageView profile_btton_back = (ImageView) view.findViewById(R.id.profile_btton_back);
        iv_profile_back =  findViewById(R.id.iv_profile_back);
        iv_profile_forward =  findViewById(R.id.iv_profile_forward);

        tv_msg = findViewById(R.id.tv_msg);
        tv_no_data_msg = findViewById(R.id.tv_no_data_msg);
        ll_progress = findViewById(R.id.ll_progress);

        ImageView ivNevMenu = findViewById(R.id.ivNevMenu);
        ivNevMenu.setVisibility(View.VISIBLE);
        ivNevMenu.setOnClickListener(this);

        NavigationView navigationView =  findViewById(R.id.nav_view);
        drawer =  findViewById(R.id.drawer_layout);
        // navigationView.setNavigationItemSelectedListener(this);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);;
        drawer.setScrimColor(getResources().getColor(R.color.dark_transperant));
        navigationView.setItemIconTintList(null);

        addItems();

        RecyclerView rycslidermenu = findViewById(R.id.rycslidermenu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MyProfileActivity.this);
        rycslidermenu.setLayoutManager(layoutManager);
        NavigationMenuAdapter listAdapter = new NavigationMenuAdapter(MyProfileActivity.this,
                navigationItems,drawer,this);

        rycslidermenu.setAdapter(listAdapter);

        final RelativeLayout rlContent = findViewById(R.id.rlContent);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                // mainView.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
                float slideX = drawerView.getWidth() * slideOffset;
                rlContent.setTranslationX(-slideX);
            }
        });
        TextView user_name = findViewById(R.id.user_name);
        CircleImageView user_image = findViewById(R.id.user_image);
        User user = Mualab.getInstance().getSessionManager().getUser();

        if (!user.profileImage.isEmpty()) {
            Picasso.with(MyProfileActivity.this).load(user.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(user_image);
        }

        user_name.setText(user.userName);


        apiForGetProfile();

        // rvFeed.setNestedScrollingEnabled(false);

       /* WrapContentLinearLayoutManager lm = new WrapContentLinearLayoutManager(MyProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        rvFeed.setItemAnimator(null);
        rvFeed.setLayoutManager(lm);
        rvFeed.setHasFixedSize(true);

        feedAdapter = new FeedAdapter(MyProfileActivity.this, feeds, this);
        endlesScrollListener = new RecyclerViewScrollListener(lm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if(feedAdapter!=null){
                    feedAdapter.showHideLoading(true);
                    apiForGetAllFeeds(page, 10, false);
                }
            }

            @Override
            public void onScroll(RecyclerView view, int dx, int dy) {

            }
        };

        rvFeed.setAdapter(feedAdapter);
        rvFeed.addOnScrollListener(endlesScrollListener);

        mRefreshLayout =  findViewById(R.id.mSwipeRefreshLayout);
        mRefreshLayout.setNestedScrollingEnabled(false);
        final CircleHeaderView header = new CircleHeaderView(MyProfileActivity.this);
        mRefreshLayout.addHeader(header);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                endlesScrollListener.resetState();
                isPulltoRefrash = true;
                apiForGetAllFeeds(0, 10, false);
            }

            @Override
            public void onLoadMore() {
                Log.e("MYPROFLIE", "onLoadMore: ");
            }
        });

        rvFeed.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                //if(e.getAction() == MotionEvent.ACTION_UP)
                //  hideQuickView();
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent event) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }});
*/
        lyImage.setOnClickListener(this);
        lyVideos.setOnClickListener(this);
        lyFeed.setOnClickListener(this);

        llServices.setOnClickListener(this);
        llCertificate.setOnClickListener(this);
        llAboutUs.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
        llPost.setOnClickListener(this);
        ivHeaderBack.setOnClickListener(this);
        //  profile_btton_back.setOnClickListener(this);
        iv_profile_back.setOnClickListener(this);
        iv_profile_forward.setOnClickListener(this);

        iv_profile_back.setEnabled(false);
        iv_profile_forward.setEnabled(true);

    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        switch (v.getId()){
            case R.id.ivNevMenu:
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                }
                else {
                    drawer.openDrawer(GravityCompat.END);
                }
                break;

            case R.id.ly_feeds:
            case R.id.ly_images:
            case R.id.ly_videos:
                updateViewType(v.getId());
                break;

            case R.id.llServices:
                //   startActivity(new Intent(MyProfileActivity.this,ArtistServicesActivity.class));
                break;

            case R.id.llAboutUs:
                MyToast.getInstance(MyProfileActivity.this).showDasuAlert("Under development");
                break;

            case R.id.llCertificate:
                //       Intent intent = new Intent(MyProfileActivity.this, CertificateActivity.class);
                //    startActivityForResult(intent, 10);
                break;

            case R.id.llFollowing:
                Intent intent1 = new Intent(MyProfileActivity.this,FollowersActivity.class);
                intent1.putExtra("isFollowers",false);
                startActivityForResult(intent1,10);
                break;

            case R.id.llFollowers:
                Intent intent2 = new Intent(MyProfileActivity.this,FollowersActivity.class);
                intent2.putExtra("isFollowers",true);
                startActivityForResult(intent2,10);
                //startActivity(new Intent(mContext,FollowersActivity.class));
                break;

            case R.id.llPost:
                updateViewType(R.id.ly_feeds);
                break;

            case R.id.iv_profile_back:
                iv_profile_back.setEnabled(false);
                iv_profile_forward.setEnabled(true);
                iv_profile_back.setColorFilter(ContextCompat.getColor(MyProfileActivity.this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                iv_profile_forward.setColorFilter(ContextCompat.getColor(MyProfileActivity.this, R.color.text_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                Animation anim = AnimationUtils.loadAnimation(MyProfileActivity.this, R.anim.move_left);
                lowerLayout1.startAnimation(anim);
                lowerLayout2.setVisibility(View.GONE);
                lowerLayout1.setVisibility(View.VISIBLE);
                tv_dot1.setBackgroundResource(R.drawable.black_circle);
                tv_dot2.setBackgroundResource(R.drawable.bg_blank_black_circle);
                break;
            case R.id.iv_profile_forward:
                iv_profile_back.setEnabled(true);
                iv_profile_forward.setEnabled(false);
                iv_profile_forward.setColorFilter(ContextCompat.getColor(MyProfileActivity.this, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                iv_profile_back.setColorFilter(ContextCompat.getColor(MyProfileActivity.this, R.color.text_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                Animation anim2 = AnimationUtils.loadAnimation(MyProfileActivity.this, R.anim.move_right);
                lowerLayout2.startAnimation(anim2);
                lowerLayout1.setVisibility(View.GONE);
                lowerLayout2.setVisibility(View.VISIBLE);
                tv_dot1.setBackgroundResource(R.drawable.bg_blank_black_circle);
                tv_dot2.setBackgroundResource(R.drawable.black_circle);
                break;

            case R.id.ivHeaderBack:
                finish();
                break;
        }
    }

    private void addItems(){
        NavigationItem item;
        for(int i=0;i<8;i++) {
            item = new NavigationItem();
            switch (i) {
                case 0:
                    item.itemName = getString(R.string.edit_profile);
                    item.itemImg = R.drawable.ic_user_prifile_ico;

                    break;
                case 1:
                    item.itemName =getString(R.string.inbox);
                    item.itemImg = R.drawable.ic_chat_ico;

                    break;

                case 2:
                    item.itemName = getString(R.string.booking);
                    item.itemImg = R.drawable.ic_booking_ico;
                    break;

                case 3:
                    item.itemName = getString(R.string.payment_history);
                    item.itemImg = R.drawable.ic_payment_ico;
                    break;

                case 4:
                    item.itemName = getString(R.string.rate_this_app);
                    item.itemImg = R.drawable.ic_star_rating_ico;
                    break;
                case 5:
                    item.itemName = getString(R.string.payment_info);
                    item.itemImg = R.drawable.ic_payment_info_ico;
                    break;
                case 6:
                    item.itemName = getString(R.string.about_koobi);
                    item.itemImg = R.drawable.ic_about;
                    break;
                case 7:
                    item.itemName = "Logout";
                    item.itemImg = R.drawable.ic_logout;

                    break;

            }
            navigationItems.add(item);
        }
    }

    private void setProfileData(UserProfileData profileData){
        TextView  tv_ProfileName =  findViewById(R.id.tv_ProfileName);
        TextView   tv_username =  findViewById(R.id.tv_username);
        TextView   tvRatingCount =  findViewById(R.id.tvRatingCount);
        TextView   tv_distance =  findViewById(R.id.tv_distance);
        TextView   tv_profile_post =  findViewById(R.id.tv_profile_post);
        TextView  tv_profile_following =  findViewById(R.id.tv_profile_following);
        TextView  tv_profile_followers =  findViewById(R.id.tv_profile_followers);
        TextView  tvServiceCount =  findViewById(R.id.tvServiceCount);
        TextView  tvCertificateCount =  findViewById(R.id.tvCertificateCount);
        CircleImageView iv_Profile =  findViewById(R.id.iv_Profile);
        ImageView ivActive =  findViewById(R.id.ivActive);
        RatingBar rating =  findViewById(R.id.rating);

        if (profileData!=null){
            tv_distance.setText(profileData.radius+" Miles");
            tv_ProfileName.setText(profileData.firstName+" "+profileData.lastName);
            tv_username.setText("@"+profileData.userName);
            tv_profile_followers.setText(profileData.followersCount);
            tv_profile_following.setText(profileData.followingCount);
            tv_profile_post.setText(profileData.postCount);
            tvRatingCount.setText("("+profileData.reviewCount+")");
            tvCertificateCount.setText(profileData.certificateCount);
            tvServiceCount.setText(profileData.serviceCount);
            rating.setRating(Float.parseFloat(profileData.ratingCount));

            if (profileData.isCertificateVerify.equals("0")){
                ivActive.setVisibility(View.VISIBLE);
            }

            Picasso.with(MyProfileActivity.this).load(profileData.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(iv_Profile);

          /*  if (!profileData.profileImage.equals("null") && !profileData.profileImage.equals("")){
                Picasso.with(mContext).load(profileData.profileImage).placeholder(R.drawable.defoult_user_img).
                        fit().into(iv_Profile);
            }else {
                iv_Profile.setImageDrawable(mContext.getResources().getDrawable(R.drawable.defoult_user_img));
            }*/
            Session session = Mualab.getInstance().getSessionManager();
            User user = session.getUser();
            if (user.userType.equals("user")){
                iv_profile_back.setVisibility(View.GONE);
                iv_profile_forward.setVisibility(View.GONE);
                lowerLayout1.setVisibility(View.GONE);
                lowerLayout2.setVisibility(View.VISIBLE);
                tv_dot1.setVisibility(View.GONE);
                tv_dot2.setVisibility(View.GONE);
                // tv_distance.setVisibility(View.GONE);
                ivActive.setVisibility(View.GONE);
            }else {
                iv_profile_back.setVisibility(View.VISIBLE);
                iv_profile_forward.setVisibility(View.VISIBLE);
                lowerLayout1.setVisibility(View.VISIBLE);
                lowerLayout2.setVisibility(View.GONE);
                tv_dot1.setVisibility(View.VISIBLE);
                tv_dot2.setVisibility(View.VISIBLE);
                // tv_distance.setVisibility(View.VISIBLE);
                ivActive.setVisibility(View.VISIBLE);
            }
        }

    }

    private void updateViewType(int id){
        tvVideos.setTextColor(getResources().getColor(R.color.text_color));
        tvImages.setTextColor(getResources().getColor(R.color.text_color));
        tvFeeds.setTextColor(getResources().getColor(R.color.text_color));
        endlesScrollListener.resetState();
        int prevSize = feeds.size();

        switch (id) {
            case R.id.ly_feeds:
                //addRemoveHeader(true);
                tvFeeds.setTextColor(getResources().getColor(R.color.colorPrimary));

                if (lastFeedTypeId != R.id.ly_feeds){
                    feeds.clear();
                    feedType = "";
                    CURRENT_FEED_STATE = Constant.FEED_STATE;
                    feedAdapter.notifyItemRangeRemoved(0, prevSize);
                    apiForGetAllFeeds(0, 10, true);
                }
                break;

            case R.id.ly_images:
                tvImages.setTextColor(getResources().getColor(R.color.colorPrimary));
                // addRemoveHeader(false);
                if (lastFeedTypeId != R.id.ly_images){
                    feeds.clear();
                    feedType = "image";
                    CURRENT_FEED_STATE = Constant.IMAGE_STATE;
                    feedAdapter.notifyItemRangeRemoved(0, prevSize);
                    apiForGetAllFeeds( 0, 10, true);
                }

                break;

            case R.id.ly_videos:
                tvVideos.setTextColor(getResources().getColor(R.color.colorPrimary));
                // addRemoveHeader(false);
                if (lastFeedTypeId != R.id.ly_videos){
                    feeds.clear();
                    feedType = "video";
                    CURRENT_FEED_STATE = Constant.VIDEO_STATE;
                    feedAdapter.notifyItemRangeRemoved(0, prevSize);
                    apiForGetAllFeeds( 0, 10, true);
                }
                break;
        }

        lastFeedTypeId = id;
        //  lastFeedTypeId = id;
    }

    private void apiForGetProfile(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyProfileActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetProfile();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("userId", String.valueOf(user.id));
        params.put("loginUserId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(MyProfileActivity.this, "getProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    if(feeds!=null && feeds.size()==0)
                        updateViewType(R.id.ly_feeds);

                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    Progress.hide(MyProfileActivity.this);
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray userDetail = js.getJSONArray("userDetail");
                        JSONObject object = userDetail.getJSONObject(0);
                        Gson gson = new Gson();

                        UserProfileData profileData = gson.fromJson(String.valueOf(object), UserProfileData.class);

                        //   profileData = gson.fromJson(response, UserProfileData.class);
                        setProfileData(profileData);
                        // updateViewType(profileData,R.id.ly_videos);

                    }else {
                        MyToast.getInstance(MyProfileActivity.this).showDasuAlert(message);
                    }
                    updateViewType(R.id.ly_feeds);
                } catch (Exception e) {
                    Progress.hide(MyProfileActivity.this);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    updateViewType(R.id.ly_feeds);
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
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(getClass().getName());
    }

    private void apiForGetAllFeeds(final int page, final int feedLimit, final boolean isEnableProgress){

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(MyProfileActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetAllFeeds(page, feedLimit, isEnableProgress);
                    }

                }
            }).show();
        }

        User currentUser = Mualab.getInstance().getSessionManager().getUser();

        Map<String, String> params = new HashMap<>();
        params.put("feedType", feedType);
        params.put("search", "");
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(feedLimit));
        // params.put("type", "newsFeed");
        params.put("userId", ""+currentUser.id);
        // params.put("appType", "user");
        Mualab.getInstance().cancelPendingRequests(this.getClass().getName());
        new HttpTask(new HttpTask.Builder(MyProfileActivity.this, "profileFeed", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                ll_progress.setVisibility(View.GONE);
                feedAdapter.showHideLoading(false);
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        //removeProgress();
                        ParseAndUpdateUI(page,response);

                    }else MyToast.getInstance(MyProfileActivity.this).showSmallMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    // MyToast.getInstance(mContext).showSmallMessage(getString(R.string.msg_some_thing_went_wrong));
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                ll_progress.setVisibility(View.GONE);
                if(isPulltoRefrash){
                    isPulltoRefrash = false;
                    mRefreshLayout.stopRefresh(false, 500);
                    int prevSize = feeds.size();
                    feeds.clear();
                    feedAdapter.notifyItemRangeRemoved(0, prevSize);
                }
                //MyToast.getInstance(mContext).showSmallMessage(getString(R.string.msg_some_thing_went_wrong));
            }})
                .setAuthToken(currentUser.authToken)
                .setParam(params)
                .setMethod(Request.Method.POST)
                .setProgress(false)
                .setBodyContentType(HttpTask.ContentType.X_WWW_FORM_URLENCODED))
                .execute("profileFeed");
        ll_progress.setVisibility(isEnableProgress?View.VISIBLE:View.GONE);
    }

    private void ParseAndUpdateUI(final int page,final String response) {

        try {
            JSONObject js = new JSONObject(response);
            String status = js.getString("status");
            // String message = js.getString("message");

            if (status.equalsIgnoreCase("success")) {
                rvFeed.setVisibility(View.VISIBLE);
                tv_no_data_msg.setVisibility(View.GONE);
                JSONArray array = js.getJSONArray("AllFeeds");

                if(isPulltoRefrash){
                    isPulltoRefrash = false;
                    mRefreshLayout.stopRefresh(true, 500);
                    int prevSize = feeds.size();
                    feeds.clear();
                    feedAdapter.notifyItemRangeRemoved(0, prevSize);
                }

                Gson gson = new Gson();
                if (array.length()!=0){
                    for (int i = 0; i < array.length(); i++) {

                        try{
                            JSONObject jsonObject = array.getJSONObject(i);
                            Feeds feed = gson.fromJson(String.valueOf(jsonObject), Feeds.class);

                            /*tmp get data and set into actual json format*/
                            if(feed.userInfo!=null && feed.userInfo.size()>0){
                                Feeds.User user = feed.userInfo.get(0);
                                feed.userName = user.userName;
                                feed.fullName = user.firstName+" "+user.lastName;
                                feed.profileImage = user.profileImage;
                                feed.userId = user._id;
                                feed.crd =feed.timeElapsed;
                            }

                            if(feed.feedData!=null && feed.feedData.size()>0){

                                feed.feed = new ArrayList<>();
                                feed.feedThumb = new ArrayList<>();

                                for(Feeds.Feed tmp : feed.feedData){
                                    feed.feed.add(tmp.feedPost);
                                    if(!TextUtils.isEmpty(feed.feedData.get(0).videoThumb))
                                        feed.feedThumb.add(tmp.feedPost);
                                }

                                if(feed.feedType.equals("video"))
                                    feed.videoThumbnail = feed.feedData.get(0).videoThumb;
                            }

                            feeds.add(feed);

                        }catch (JsonParseException e){
                            e.printStackTrace();
                        }
                    }
                }else if (page==0 || feeds.size()==0){
                    tv_no_data_msg.setVisibility(View.VISIBLE);
                    rvFeed.setVisibility(View.GONE);
                }
                // loop end.

                feedAdapter.notifyDataSetChanged();
                //updateViewType(R.id.ly_feeds);

            } else if (status.equals("fail") && feeds.size()==0) {
                rvFeed.setVisibility(View.GONE);
                tv_msg.setVisibility(View.VISIBLE);

                if(isPulltoRefrash){
                    isPulltoRefrash = false;
                    mRefreshLayout.stopRefresh(false, 500);

                }
                feedAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            feedAdapter.notifyDataSetChanged();
            //MyToast.getInstance(mContext).showSmallCustomToast(getString(R.string.alert_something_wenjt_wrong));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tvImages = null;
        tvVideos = null;
        tvFeeds = null;
        tv_msg = null;
        ll_progress = null;
        endlesScrollListener = null;
        feedAdapter = null;
        feeds = null;
        rvFeed = null;
    }

    @Override
    public void onCommentBtnClick(Feeds feed, int pos) {
        Intent intent = new Intent(MyProfileActivity.this, CommentsActivity.class);
        intent.putExtra("feed_id", feed._id);
        intent.putExtra("feedPosition", pos);
        intent.putExtra("feed", feed);
        startActivityForResult(intent, Constant.ACTIVITY_COMMENT);
    }

    @Override
    public void onLikeListClick(Feeds feed) {
       /* User currentUser = Mualab.getInstance().getSessionManager().getUser();
        if(mContext instanceof ProfileActivity){
            ((ProfileActivity) mContext).addFragment(LikeFragment.newInstance(feed._id, Integer.parseInt(currentUser.id)), true);
        }*/
    }

    @Override
    public void onFeedClick(Feeds feed, int index, View v) {
        // publicationQuickView(feed, index);
    }

    @Override
    public void onClickProfileImage(Feeds feed, ImageView v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            apiForGetProfile();
            // apiForGetAllFeeds(0, 10, true);
        }
    }

    @Override
    public void OnClick(int pos) {

    }
}
