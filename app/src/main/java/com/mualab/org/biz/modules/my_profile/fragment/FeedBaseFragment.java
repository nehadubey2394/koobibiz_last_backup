package com.mualab.org.biz.modules.my_profile.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.modules.my_profile.model.Feeds;
import com.mualab.org.biz.modules.Base.BaseFragment;
import com.squareup.picasso.Picasso;


public class FeedBaseFragment extends BaseFragment {

    public FeedBaseFragment(){

    }

    /*Rj*/
    private Dialog builder;
    public void publicationQuickView(Feeds feeds, int index){
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate( R.layout.dialog_image_detail_view, null);

        ImageView postImage = view.findViewById(R.id.ivFeedCenter);
        ImageView profileImage =  view.findViewById(R.id.ivUserProfile);
        TextView tvUsername =  view.findViewById(R.id.txtUsername);
        tvUsername.setText(feeds.userName);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideQuickView();
            }
        });

        view.findViewById(R.id.tvUnfollow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToast.getInstance(mContext).showSmallCustomToast(getString(R.string.under_development));
            }
        });

        Picasso.with(mContext).load(feeds.feed.get(index)).priority(Picasso.Priority.HIGH).noPlaceholder().into(postImage);

        if(TextUtils.isEmpty(feeds.profileImage))
            Picasso.with(mContext).load(R.drawable.defoult_user_img).noPlaceholder().into(profileImage);
        else Picasso.with(mContext).load(feeds.profileImage).noPlaceholder().into(profileImage);

        builder = new Dialog(mContext);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //noinspection ConstantConditions
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);
        builder.setCancelable(true);
        builder.show();
    }

    public void hideQuickView(){
        if(builder != null) builder.dismiss();
    }
}
