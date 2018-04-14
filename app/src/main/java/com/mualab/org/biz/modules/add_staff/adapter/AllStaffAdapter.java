package com.mualab.org.biz.modules.add_staff.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.add_staff.AllArtist;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.add_staff.activity.AddStaffActivity;
import com.mualab.org.biz.modules.add_staff.activity.AddStaffDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class AllStaffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AllArtist> staffList;
    private boolean showLoader;

    private  final int VIEWTYPE_ITEM = 1;
    private  final int VIEWTYPE_LOADER = 2;

    // Constructor of the class
    public AllStaffAdapter(Context context, List<AllArtist> staffList) {
        this.context = context;
        this.staffList = staffList;
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_staff_item_layout, parent, false);
        // return new ViewHolder(view);
        View view;
        switch (viewType) {
            case VIEWTYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_staff_item_layout, parent, false);
                return new ViewHolder(view);

            case VIEWTYPE_LOADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_view, parent, false);
                return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {
        if(position==staffList.size()-1){
            return showLoader?VIEWTYPE_LOADER:VIEWTYPE_ITEM;
        }
        return VIEWTYPE_ITEM;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loaderViewHolder = (LoadingViewHolder) viewHolder;
            if (showLoader) {
                loaderViewHolder.progressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.progressBar.setVisibility(View.GONE);
            }
            return;
        }

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final AllArtist item = staffList.get(position);

        holder.tvStaffServices.setVisibility(View.GONE);
        holder.tvStaffName.setText(item.userName);

        if (!item.profileImage.equals("")){
            Picasso.with(context).load(item.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(holder.ivStaffProfile);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvStaffServices,tvStaffName;
        ImageView ivStaffProfile;
        private ViewHolder(View itemView)
        {
            super(itemView);

            ivStaffProfile = itemView.findViewById(R.id.ivStaffProfile);
            tvStaffServices = itemView.findViewById(R.id.tvStaffServices);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            AllArtist artistStaff = staffList.get(getAdapterPosition());
            StaffDetail item = new StaffDetail();

            item.userName = artistStaff.userName;
            item.profileImage = artistStaff.profileImage;
            item.staffId = artistStaff._id;

            Intent intent = new Intent(context, AddStaffDetailActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("staff",item);
            intent.putExtra("BUNDLE",args);
            context.startActivity(intent);
            //((AddStaffActivity)context).finish();
        }
    }

}