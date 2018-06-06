package com.mualab.org.biz.modules.my_profile.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.UserProfileData;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.my_profile.activity.ProfileActivity;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private String mParam1;
    private Context mContext;
    private TextView tvImages,tvVideos,tvFeeds,tv_dot1,tv_dot2,tv_gender,tvCertificateCount,tvServiceCount,tvRatingCount,tv_username,tv_ProfileName,
            tv_profile_following,tv_profile_followers,tv_profile_post,tv_distance;
    private ImageView iv_profile_back ,iv_profile_forward,ivActive;
    private CircleImageView iv_Profile;
    private LinearLayout lyImage,lyVideos,lyFeed,lowerLayout2,lowerLayout1;
    private RecyclerView rvFeed;
    private RatingBar rating;

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
        if (getArguments() != null) {
            //  mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(View view){
        rvFeed = (RecyclerView) view.findViewById(R.id.recyclerView);
   /*     rvFeed.setLayoutManager(new GridLayoutManager(mContext, 3));
        imagesAdapter = new ImagesAdapter(mContext,allfeedsList, IMAGE_STATE);*/

        rvFeed.setNestedScrollingEnabled(false);
        /*rvFeed.setHasFixedSize(true);
        rvFeed.setItemViewCacheSize(20);
        rvFeed.setDrawingCacheEnabled(true);
        rvFeed.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/

        iv_Profile =  view.findViewById(R.id.iv_Profile);
        ivActive =  view.findViewById(R.id.ivActive);

        rating =  view.findViewById(R.id.rating);

        tvImages = view.findViewById(R.id.tv_image);
        tvVideos = view.findViewById(R.id.tv_videos);
        tvFeeds =  view.findViewById(R.id.tv_feed);
        tv_ProfileName =  view.findViewById(R.id.tv_ProfileName);
        tv_username =  view.findViewById(R.id.tv_username);
        tvRatingCount =  view.findViewById(R.id.tvRatingCount);
        lyImage =  view.findViewById(R.id.ly_images);
        lyVideos = view.findViewById(R.id.ly_videos);
        lyFeed =  view.findViewById(R.id.ly_feeds);

        tv_dot1 =  view.findViewById(R.id.tv_dot1);
        tv_dot2 =  view.findViewById(R.id.tv_dot2);
        tv_distance =  view.findViewById(R.id.tv_distance);
        tv_profile_post =  view.findViewById(R.id.tv_profile_post);
        tv_profile_following =  view.findViewById(R.id.tv_profile_following);
        tv_profile_followers =  view.findViewById(R.id.tv_profile_followers);
        tvServiceCount =  view.findViewById(R.id.tvServiceCount);
        tvCertificateCount =  view.findViewById(R.id.tvCertificateCount);

        lyImage =  view.findViewById(R.id.ly_images);
        lyVideos = view.findViewById(R.id.ly_videos);
        lyFeed =  view.findViewById(R.id.ly_feeds);

        lowerLayout2 =  view.findViewById(R.id.lowerLayout2);
        lowerLayout1 =  view.findViewById(R.id.lowerLayout);

        //  ImageView profile_btton_back = (ImageView) view.findViewById(R.id.profile_btton_back);
        iv_profile_back =  view.findViewById(R.id.iv_profile_back);
        iv_profile_forward =  view.findViewById(R.id.iv_profile_forward);

        lyImage.setOnClickListener(this);
        lyVideos.setOnClickListener(this);
        lyFeed.setOnClickListener(this);
        //  profile_btton_back.setOnClickListener(this);
        iv_profile_back.setOnClickListener(this);
        iv_profile_forward.setOnClickListener(this);

        iv_profile_back.setEnabled(false);
        iv_profile_forward.setEnabled(true);

        apiForGetProfile();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlProfile:
                startActivity(new Intent(mContext,ProfileActivity.class));
                break;
            case R.id.iv_profile_back:
                iv_profile_back.setEnabled(false);
                iv_profile_forward.setClickable(true);
                iv_profile_back.setClickable(false);
                iv_profile_forward.setEnabled(true);
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
                iv_profile_back.setClickable(true);
                iv_profile_forward.setClickable(false);
                Animation anim2 = AnimationUtils.loadAnimation(mContext, R.anim.move_right);
                lowerLayout2.startAnimation(anim2);
                lowerLayout1.setVisibility(View.GONE);
                lowerLayout2.setVisibility(View.VISIBLE);
                tv_dot1.setBackgroundResource(R.drawable.bg_blank_black_circle);
                tv_dot2.setBackgroundResource(R.drawable.black_circle);
                break;
        }
    }


    private void updateViewType(UserProfileData profileData,int id){
        // ivImages.setImageResource(R.drawable.active_picture_icon);
        // tvImages.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
        // lyImage.setBackgroundResource(R.drawable.rectangular_grey_border);


        // ivVideos.setImageResource(R.drawable.active_video_icon);
        // tvVideos.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
        // lyVideos.setBackgroundResource(R.drawable.rectangular_grey_border);

        // ivFeeds.setImageResource(R.drawable.feeds_icon);
        // tvFeeds.setTextColor(ContextCompat.getColor(mContext,R.color.gray));
        // lyFeed.setBackgroundResource(R.drawable.rectangular_grey_border);

        int numberOfColumns = 3;

        if (profileData!=null){
            tv_ProfileName.setText(profileData.firstName);
            tv_username.setText(profileData.userName);
            tv_profile_followers.setText(profileData.followersCount);
            tv_profile_following.setText(profileData.followingCount);
            tv_profile_post.setText(profileData.postCount);
            tvRatingCount.setText("("+profileData.reviewCount+")");
            tvCertificateCount.setText(profileData.certificateCount);
            tvServiceCount.setText(profileData.serviceCount);
            rating.setRating(Float.parseFloat(profileData.ratingCount));

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
                tv_distance.setVisibility(View.GONE);
                ivActive.setVisibility(View.GONE);
            }else {
                iv_profile_back.setVisibility(View.VISIBLE);
                iv_profile_forward.setVisibility(View.VISIBLE);
                lowerLayout1.setVisibility(View.VISIBLE);
                lowerLayout2.setVisibility(View.GONE);
                tv_dot1.setVisibility(View.VISIBLE);
                tv_dot2.setVisibility(View.VISIBLE);
                tv_distance.setVisibility(View.VISIBLE);
                ivActive.setVisibility(View.VISIBLE);
            }
        }

        switch (id){
            case R.id.ly_images:
               /* ivImages.setImageResource(R.drawable.inactive_picture_icon);
                tvImages.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                setGradent(tvImages, startEndColor);
                //  lyImage.setBackgroundResource(R.drawable.rectangular_feed_primary_border);
                *//*imagesAdapter = new ImagesAdapter(mContext,allfeedsList,IMAGE_STATE);
                rvFeed.setAdapter(imagesAdapter);*//*
                imagesAdapter = new ImagesAdapter(mContext,allfeedsList,IMAGE_STATE);
                rvFeed.setAdapter(imagesAdapter);
                // setUpRecycleView(true, imagesAdapter);
                if(lastFeedTypeId!= R.id.ly_images)
                    apiForImageAndVideos("image","","0","10",IMAGE_STATE);*/
                break;

            case R.id.ly_videos:
              /*  ivVideos.setImageResource(R.drawable.video_icon);
                // tvVideos.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                //lyVideos.setBackgroundResource(R.drawable.rectangular_feed_primary_border);
                setGradent(tvVideos, startEndColor);

                imagesAdapter = new ImagesAdapter(mContext,allfeedsList,VIDEO_STATE);
                rvFeed.setLayoutManager(new GridLayoutManager(mContext, numberOfColumns, LinearLayoutManager.VERTICAL,false));
                rvFeed.setAdapter(imagesAdapter);

                *//*imagesAdapter = new ImagesAdapter(mContext,allfeedsList,VIDEO_STATE);
                setUpRecycleView(true,imagesAdapter);*//*

                if(lastFeedTypeId!= R.id.ly_videos)
                    apiForImageAndVideos("video","","0","10",VIDEO_STATE);*/
                break;

            case R.id.ly_feeds:
             /*   ivFeeds.setImageResource(R.drawable.video_icon);
                // tvFeeds.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                // lyFeed.setBackgroundResource(R.drawable.rectangular_feed_primary_border);
                setGradent(tvFeeds, startEndColor);

                feedAdapter = new FeedAdapter(mContext, allfeedsList);
                rvFeed.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false));
                rvFeed.setAdapter(feedAdapter);

                *//*feedAdapter = new FeedAdapter(mContext, allfeedsList);
                setUpRecycleView(false, feedAdapter);*//*
                apiForImageAndVideos("","","0","50",FEED_STATE);*/
                break;
        }
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

                    if (status.equalsIgnoreCase("success")) {
                        JSONArray userDetail = js.getJSONArray("userDetail");
                        JSONObject object = userDetail.getJSONObject(0);
                        Gson gson = new Gson();
                        UserProfileData profileData = gson.fromJson(String.valueOf(object), UserProfileData.class);

                        //   profileData = gson.fromJson(response, UserProfileData.class);
                        updateViewType(profileData,R.id.ly_videos);

                    }else {
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(mContext);
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

}
