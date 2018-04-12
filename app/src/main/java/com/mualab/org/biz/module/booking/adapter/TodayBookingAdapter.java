package com.mualab.org.biz.module.booking.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.module.booking.listner.OnBookingListListener;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Bookings;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.session.Session;
import com.squareup.picasso.Picasso;

import java.util.List;


public class TodayBookingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Bookings> artistsList;
    private List<Staff> staffList;
    private OnBookingListListener bookingListListener;

    // Constructor of the class
    public TodayBookingAdapter(Context context, List<Bookings> artistsList,List<Staff> staffList) {
        this.context = context;
        this.artistsList = artistsList;
        this.staffList = staffList;
    }

    public  void setBookingClickListner(OnBookingListListener bookingListListener){
        this.bookingListListener = bookingListListener;
    }
    @Override
    public int getItemCount() {
        return artistsList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_for_todaylist, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final Bookings item = artistsList.get(position);

        if (item.bookStatus.equals("2")) {
            holder.tvBookingStatus.setText(R.string.text_cancelled);
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.primary_red));
        }else {
            holder.tvBookingStatus.setText(R.string.text_confirmed);
            holder.tvBookingStatus.setTextColor(context.getResources().getColor(R.color.primary_green));
        }

        holder.tvUserName.setText(item.userDetail.userName);
        holder.tvServices.setText(item.artistServiceName);
        holder.tvTime.setText(item.bookingTime);
        holder.tvStaffName.setText(item.todayBookingInfos.get(0).staffName);

        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (user.businessType.equals("independent"))
            holder.rlStaffName.setVisibility(View.GONE);
        else
            holder.rlStaffName.setVisibility(View.VISIBLE);

        if (!item.userDetail.profileImage.equals("")){
            Picasso.with(context).load(item.userDetail.profileImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(holder.ivProfilePic);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvStaffName,tvUserName,tvServices,tvBookingStatus,tvTime;
        ImageView ivProfilePic;
        RelativeLayout rlStaffName;
        private ViewHolder(View itemView)
        {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvServices = itemView.findViewById(R.id.tvServices);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            rlStaffName = itemView.findViewById(R.id.rlStaffName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bookings info = artistsList.get(getAdapterPosition());
            if (bookingListListener!=null){
                bookingListListener.onItemClick(getAdapterPosition(),staffList,info._id);
            }

           /* Intent intent = new Intent(context, BookingDetailActivity.class);
            Bundle args = new Bundle();
            args.putSerializable("ARRAYLIST",(Serializable)staffList);
            intent.putExtra("BUNDLE",args);
            intent.putExtra("bookingId",info._id);
            ((MainActivity)context).startActivityForResult(intent,2);*/

        }
    }

}