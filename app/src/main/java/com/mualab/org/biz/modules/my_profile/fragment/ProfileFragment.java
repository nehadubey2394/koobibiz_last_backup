package com.mualab.org.biz.modules.my_profile.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.mualab.org.biz.modules.my_profile.model.UserProfileData;
import com.mualab.org.biz.modules.my_profile.model.Feeds;
import com.mualab.org.biz.modules.my_profile.activity.ArtistServicesActivity;
import com.mualab.org.biz.modules.my_profile.adapter.feeds.FeedAdapter;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.WrapContentLinearLayoutManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import views.refreshview.CircleHeaderView;
import views.refreshview.OnRefreshListener;
import views.refreshview.RjRefreshLayout;


public class ProfileFragment extends FeedBaseFragment implements View.OnClickListener,FeedAdapter.Listener {
    private String mParam1,TAG = this.getClass().getName();
    private Context mContext;
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

    private View view;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        //args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feeds = new ArrayList<>();
        if (getArguments() != null) {
            //  mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvFeed =  view.findViewById(R.id.rvFeed);
        // rvFeed.setNestedScrollingEnabled(false);

        WrapContentLinearLayoutManager lm = new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rvFeed.setItemAnimator(null);
        rvFeed.setLayoutManager(lm);
        rvFeed.setHasFixedSize(true);

        apiForGetProfile();

        feedAdapter = new FeedAdapter(mContext, feeds, this);
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

        //  if(feeds!=null && feeds.size()==0)
        //  updateViewType(R.id.ly_feeds);

        mRefreshLayout =  view.findViewById(R.id.mSwipeRefreshLayout);
        mRefreshLayout.setNestedScrollingEnabled(false);
        final CircleHeaderView header = new CircleHeaderView(getContext());
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
                Log.e(TAG, "onLoadMore: ");
            }
        });

        rvFeed.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                if(e.getAction() == MotionEvent.ACTION_UP)
                    hideQuickView();
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent event) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }});
    }

    private void initView(View view){

        tvImages = view.findViewById(R.id.tv_image);
        tvVideos = view.findViewById(R.id.tv_videos);
        tvFeeds =  view.findViewById(R.id.tv_feed);
        tv_dot1 =  view.findViewById(R.id.tv_dot1);
        tv_dot2 =  view.findViewById(R.id.tv_dot2);

        LinearLayout lyImage = view.findViewById(R.id.ly_images);
        LinearLayout lyVideos = view.findViewById(R.id.ly_videos);
        LinearLayout lyFeed = view.findViewById(R.id.ly_feeds);

        LinearLayout llServices = view.findViewById(R.id.llServices);
        LinearLayout llCertificate = view.findViewById(R.id.llCertificate);
        LinearLayout llAboutUs = view.findViewById(R.id.llAboutUs);
        LinearLayout llFollowers = view.findViewById(R.id.llFollowers);
        LinearLayout llFollowing = view.findViewById(R.id.llFollowing);
        LinearLayout llPost = view.findViewById(R.id.llPost);

        lowerLayout2 =  view.findViewById(R.id.lowerLayout2);
        lowerLayout1 =  view.findViewById(R.id.lowerLayout);

        //  ImageView profile_btton_back = (ImageView) view.findViewById(R.id.profile_btton_back);
        iv_profile_back =  view.findViewById(R.id.iv_profile_back);
        iv_profile_forward =  view.findViewById(R.id.iv_profile_forward);

        tv_msg = view.findViewById(R.id.tv_msg);
        tv_no_data_msg = view.findViewById(R.id.tv_no_data_msg);
        ll_progress = view.findViewById(R.id.ll_progress);

        lyImage.setOnClickListener(this);
        lyVideos.setOnClickListener(this);
        lyFeed.setOnClickListener(this);

        llServices.setOnClickListener(this);
        llCertificate.setOnClickListener(this);
        llAboutUs.setOnClickListener(this);
        llFollowers.setOnClickListener(this);
        llFollowing.setOnClickListener(this);
        llPost.setOnClickListener(this);
        //  profile_btton_back.setOnClickListener(this);
        iv_profile_back.setOnClickListener(this);
        iv_profile_forward.setOnClickListener(this);

        iv_profile_back.setEnabled(false);
        iv_profile_forward.setEnabled(true);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlProfile:
                //      startActivity(new Intent(mContext,ProfileActivity.class));
                break;

            case R.id.ly_feeds:
            case R.id.ly_images:
            case R.id.ly_videos:
                updateViewType(view.getId());
                break;

            case R.id.llServices:
                startActivity(new Intent(mContext,ArtistServicesActivity.class));
                break;

            case R.id.llAboutUs:
                MyToast.getInstance(mContext).showDasuAlert("Under development");
                break;

            case R.id.llCertificate:
                MyToast.getInstance(mContext).showDasuAlert("Under development");
                break;

            case R.id.llFollowing:
                Intent intent1 = new Intent(mContext,FollowersActivity.class);
                intent1.putExtra("isFollowers",false);
                startActivity(intent1);
                break;

            case R.id.llFollowers:
                Intent intent = new Intent(mContext,FollowersActivity.class);
                intent.putExtra("isFollowers",true);
                startActivity(intent);
                //startActivity(new Intent(mContext,FollowersActivity.class));
                break;

            case R.id.llPost:
                updateViewType(R.id.ly_feeds);
                break;

            case R.id.iv_profile_back:
                iv_profile_back.setEnabled(false);
                iv_profile_forward.setEnabled(true);
                iv_profile_back.setColorFilter(ContextCompat.getColor(mContext, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                iv_profile_forward.setColorFilter(ContextCompat.getColor(mContext, R.color.text_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.move_left);
                lowerLayout1.startAnimation(anim);
                lowerLayout2.setVisibility(View.GONE);
                lowerLayout1.setVisibility(View.VISIBLE);
                tv_dot1.setBackgroundResource(R.drawable.black_circle);
                tv_dot2.setBackgroundResource(R.drawable.bg_blank_black_circle);
                break;
            case R.id.iv_profile_forward:
                iv_profile_back.setEnabled(true);
                iv_profile_forward.setEnabled(false);
                iv_profile_forward.setColorFilter(ContextCompat.getColor(mContext, R.color.gray), android.graphics.PorterDuff.Mode.MULTIPLY);
                iv_profile_back.setColorFilter(ContextCompat.getColor(mContext, R.color.text_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                Animation anim2 = AnimationUtils.loadAnimation(mContext, R.anim.move_right);
                lowerLayout2.startAnimation(anim2);
                lowerLayout1.setVisibility(View.GONE);
                lowerLayout2.setVisibility(View.VISIBLE);
                tv_dot1.setBackgroundResource(R.drawable.bg_blank_black_circle);
                tv_dot2.setBackgroundResource(R.drawable.black_circle);
                break;
        }
    }

    private void setProfileData(UserProfileData profileData){
        TextView  tv_ProfileName =  view.findViewById(R.id.tv_ProfileName);
        TextView   tv_username =  view.findViewById(R.id.tv_username);
        TextView   tvRatingCount =  view.findViewById(R.id.tvRatingCount);
        TextView   tv_distance =  view.findViewById(R.id.tv_distance);
        TextView   tv_profile_post =  view.findViewById(R.id.tv_profile_post);
        TextView  tv_profile_following =  view.findViewById(R.id.tv_profile_following);
        TextView  tv_profile_followers =  view.findViewById(R.id.tv_profile_followers);
        TextView  tvServiceCount =  view.findViewById(R.id.tvServiceCount);
        TextView  tvCertificateCount =  view.findViewById(R.id.tvCertificateCount);
        CircleImageView iv_Profile =  view.findViewById(R.id.iv_Profile);
        ImageView ivActive =  view.findViewById(R.id.ivActive);
        RatingBar rating =  view.findViewById(R.id.rating);

        if (profileData!=null){
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

            Picasso.with(mContext).load(profileData.profileImage).placeholder(R.drawable.defoult_user_img).
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
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
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

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "getProfile", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    Progress.hide(mContext);
                    if (status.equalsIgnoreCase("success")) {
                        JSONArray userDetail = js.getJSONArray("userDetail");
                        JSONObject object = userDetail.getJSONObject(0);
                        Gson gson = new Gson();

                        UserProfileData profileData = gson.fromJson(String.valueOf(object), UserProfileData.class);

                        //   profileData = gson.fromJson(response, UserProfileData.class);
                        setProfileData(profileData);
                        // updateViewType(profileData,R.id.ly_videos);

                    }else {
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    }
                    updateViewType(R.id.ly_feeds);
                } catch (Exception e) {
                    Progress.hide(mContext);
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
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(TAG);
    }

    private void apiForGetAllFeeds(final int page, final int feedLimit, final boolean isEnableProgress){
        tv_no_data_msg.setVisibility(View.GONE);
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
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
        new HttpTask(new HttpTask.Builder(mContext, "profileFeed", new HttpResponceListner.Listener() {
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

                    }else MyToast.getInstance(mContext).showSmallMessage(message);
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
                .execute(TAG);
        ll_progress.setVisibility(isEnableProgress?View.VISIBLE:View.GONE);
    }

    private void ParseAndUpdateUI(final int page,final String response) {

        try {
            JSONObject js = new JSONObject(response);
            String status = js.getString("status");
            // String message = js.getString("message");

            if (status.equalsIgnoreCase("success")) {
                rvFeed.setVisibility(View.VISIBLE);
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
    public void onDestroyView() {
        super.onDestroyView();
        Mualab.getInstance().cancelPendingRequests(TAG);
        mContext = null;
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
        Intent intent = new Intent(mContext, CommentsActivity.class);
        intent.putExtra("feed_id", feed._id);
        intent.putExtra("feedPosition", pos);
        intent.putExtra("feed", feed);
        startActivityForResult(intent, Constant.ACTIVITY_COMMENT);
    }

    @Override
    public void onLikeListClick(Feeds feed) {

    }

    @Override
    public void onFeedClick(Feeds feed, int index, View v) {
        publicationQuickView(feed, index);
    }

    @Override
    public void onClickProfileImage(Feeds feed, ImageView v) {

    }
}
