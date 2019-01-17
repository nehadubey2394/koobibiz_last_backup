package com.mualab.org.biz.modules.new_booking.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.add_staff.adapter.LoadingViewHolder;
import com.mualab.org.biz.modules.base.ItemClickListener;
import com.mualab.org.biz.modules.new_booking.model.BookingHistory;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by hemant
 * Date: 17/4/18
 * Time: 4:03 PM
 */

public class BookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEWTYPE_ITEM = 1;
    private final int VIEWTYPE_LOADER = 2;
    private List<BookingHistory.DataBean> list;
    private boolean showLoader;
    private ItemClickListener clickListener;

    private int lastPos = -1;
    private boolean isClick = false;

    public BookingsAdapter(List<BookingHistory.DataBean> list, ItemClickListener clickListener) {
        this.list = list;
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rvHolder, int position) {
        if (rvHolder instanceof LoadingViewHolder) {
            LoadingViewHolder loaderViewHolder = (LoadingViewHolder) rvHolder;
            if (showLoader) {
                loaderViewHolder.progressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.progressBar.setVisibility(View.GONE);
            }
            return;
        }
        ViewHolder holder = ((ViewHolder) rvHolder);

        BookingHistory.DataBean mainBean = list.get(position);

        Picasso.with(holder.ivProfilePic.getContext()).load(mainBean.getUserDetail().get(0).getProfileImage()).placeholder(R.drawable.defoult_user_img).into(holder.ivProfilePic);

        holder.tvUserName.setText(mainBean.getUserDetail().get(0).getUserName());
        holder.tvBDate.setText(mainBean.getCreationDate());
        holder.tvBTime.setText(mainBean.getCreationTime());

        try {
            holder.tvName.setText(mainBean.getBookingInfo().get(0).getStaffName());
            holder.tvService.setText(mainBean.getBookingInfo().get(0).getArtistServiceName());
            String serviceDate = "" + mainBean.getBookingInfo().get(0).getBookingDate() + " " +
                    mainBean.getBookingInfo().get(0).getStartTime() + " " + mainBean.getBookingInfo().get(0).getEndTime();
            holder.tvServiceDate.setText(serviceDate);
        } catch (Exception ignored) {
        }
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEWTYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookings, parent, false);
                return new ViewHolder(view);

            case VIEWTYPE_LOADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_view, parent, false);
                return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size() - 1) {
            return showLoader ? VIEWTYPE_LOADER : VIEWTYPE_ITEM;
        }
        return VIEWTYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfilePic;
        private TextView tvUserName, tvName, tvService, tvServiceDate, tvBDate, tvBTime;

        ViewHolder(@NonNull View view) {
            super(view);

            ivProfilePic = view.findViewById(R.id.ivProfilePic);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvName = view.findViewById(R.id.tvName);
            tvService = view.findViewById(R.id.tvService);
            tvServiceDate = view.findViewById(R.id.tvServiceDate);
            tvBDate = view.findViewById(R.id.tvBDate);
            tvBTime = view.findViewById(R.id.tvBTime);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull final View view) {
            switch (view.getId()) {

                default:
                    if (!isClick) {
                        isClick = true;
                        if (getAdapterPosition() != -1 && clickListener != null)
                            clickListener.onItemClick(getAdapterPosition());
                    }

                    new Handler().postDelayed(() -> isClick = false, 3000);
                    break;
            }
        }
    }
}

