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
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.modules.add_staff.adapter.LoadingViewHolder;
import com.mualab.org.biz.modules.base.ItemClickListener;
import com.mualab.org.biz.modules.new_booking.model.BookingDetail;
import com.mualab.org.biz.util.CalanderUtils;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by hemant
 * Date: 17/4/18
 * Time: 4:03 PM
 */

public class ServiceAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEWTYPE_LOADER = 2;
    private List<BookingDetail.DataBean> bookingInfoBeanList;
    private boolean showLoader;
    private ItemClickListener clickListener;

    private int lastPos = -1;
    private boolean isClick = false;

    public ServiceAppointmentAdapter(List<BookingDetail.DataBean> bookingInfoBeanList, ItemClickListener clickListener) {
        this.bookingInfoBeanList = bookingInfoBeanList;
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

        BookingDetail.DataBean.BookingInfoBean mainBean = bookingInfoBeanList.get(0).getBookingInfo().get(position);
        BookingDetail.DataBean.ArtistDetailBean artistDetailBean = bookingInfoBeanList.get(0).getArtistDetail().get(position);

        try {
            if (!artistDetailBean.getProfileImage().isEmpty())
                Picasso.with(holder.ivArtistProfilePic.getContext()).load(artistDetailBean.getProfileImage()).placeholder(R.drawable.defoult_user_img).into(holder.ivArtistProfilePic);
            holder.tvArtistName.setText(artistDetailBean.getUserName());
        } catch (Exception ignored) {
        }

        Picasso.with(holder.ivStaffProfilePic.getContext()).load(mainBean.getStaffImage()).placeholder(R.drawable.defoult_user_img).into(holder.ivStaffProfilePic);

        holder.tvStaffName.setText(mainBean.getStaffName());
        holder.tvService.setText(mainBean.getArtistServiceName());
        holder.tvPrice.setText(String.format("%s", holder.tvPrice.getContext().getString(R.string.pound_symbol).concat(mainBean.getBookingPrice())));
        String bDate = mainBean.getBookingDate();
        String date = bDate.contains("-") ? CalanderUtils.formatDate(bDate, Constants.SERVER_TIMESTAMP_FORMAT, Constants.TIMESTAMP_FORMAT) : bDate;
        String serviceDate = "" + date + " " +
                mainBean.getStartTime().toLowerCase() + " " + mainBean.getEndTime().toLowerCase();
        holder.tvDateTime.setText(serviceDate);
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_appointment, parent, false);
                return new ViewHolder(view);

            case VIEWTYPE_LOADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_view, parent, false);
                return new LoadingViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        int VIEWTYPE_ITEM = 1;
        int temp = bookingInfoBeanList.isEmpty() ? 0 : bookingInfoBeanList.get(0).getBookingInfo().size() - 1;
        if (position == temp) {
            return showLoader ? VIEWTYPE_LOADER : VIEWTYPE_ITEM;
        }
        return VIEWTYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return bookingInfoBeanList.isEmpty() ? 0 : bookingInfoBeanList.get(0).getBookingInfo().size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivArtistProfilePic, ivStaffProfilePic;
        private TextView tvArtistName, tvStaffName, tvService, tvPrice, tvDateTime;

        ViewHolder(@NonNull View view) {
            super(view);

            ivArtistProfilePic = view.findViewById(R.id.ivArtistProfilePic);
            ivStaffProfilePic = view.findViewById(R.id.ivStaffProfilePic);
            tvArtistName = view.findViewById(R.id.tvArtistName);
            tvStaffName = view.findViewById(R.id.tvStaffName);
            tvService = view.findViewById(R.id.tvService);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvDateTime = view.findViewById(R.id.tvDateTime);
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

