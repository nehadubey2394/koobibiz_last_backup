package com.mualab.org.biz.module.add_staff.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.booking.Staff;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ArtistStaffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Staff> staffList;

    // Constructor of the class
    public ArtistStaffAdapter(Context context,  List<Staff> staffList) {
        this.context = context;
        this.staffList = staffList;
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_staff_item_layout, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final Staff item = staffList.get(position);

        holder.tvStaffServices.setText(item.job);
        holder.tvStaffName.setText(item.staffName);

        if (!item.staffImage.equals("")){
            Picasso.with(context).load(item.staffImage).placeholder(R.drawable.defoult_user_img).
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
            Staff artistStaff = staffList.get(getAdapterPosition());

        }
    }

}