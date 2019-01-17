package com.mualab.org.biz.modules.new_booking.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.modules.add_staff.adapter.LoadingViewHolder;
import com.mualab.org.biz.modules.new_booking.model.BookingHistory;
import com.mualab.org.biz.util.CalanderUtils;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by hemant
 * Date: 17/4/18
 * Time: 4:03 PM
 */

public class PendBookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEWTYPE_LOADER = 2;
    private List<BookingHistory.DataBean> list;
    private boolean showLoader;
    private ClickListener clickListener;

    public PendBookingsAdapter(List<BookingHistory.DataBean> list, ClickListener clickListener) {
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
            holder.tvStaffName.setText(mainBean.getBookingInfo().get(0).getStaffName());
            holder.tvService.setText(mainBean.getBookingInfo().get(0).getArtistServiceName());
            String bDate = mainBean.getBookingInfo().get(0).getBookingDate();
            String date = bDate.contains("-") ? CalanderUtils.formatDate(bDate, Constants.SERVER_TIMESTAMP_FORMAT, Constants.TIMESTAMP_FORMAT) : bDate;
            String serviceDate = date + " " +
                    mainBean.getBookingInfo().get(0).getStartTime().toLowerCase() + " " + mainBean.getBookingInfo().get(0).getEndTime().toLowerCase();
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
            case VIEWTYPE_LOADER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_view, parent, false);
                return new LoadingViewHolder(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_booking, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int VIEWTYPE_ITEM = 1;
        if (position == list.size() - 1) {
            return showLoader ? VIEWTYPE_LOADER : VIEWTYPE_ITEM;
        }
        return VIEWTYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface ClickListener {
        void onItemClick(int pos);

        void onAcceptClick(int pos);

        void onRejectClick(int pos);

        void onRescheduleClick(int pos);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivProfilePic;
        private TextView tvStaffName, tvUserName, tvService, tvServiceDate, tvBDate, tvBTime;
        private Button btnAccept, btnReject, btnReSchedule;

        ViewHolder(@NonNull View view) {
            super(view);

            ivProfilePic = view.findViewById(R.id.ivProfilePic);
            tvStaffName = view.findViewById(R.id.tvStaffName);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvService = view.findViewById(R.id.tvService);
            tvServiceDate = view.findViewById(R.id.tvServiceDate);
            tvBDate = view.findViewById(R.id.tvBDate);
            tvBTime = view.findViewById(R.id.tvBTime);

            btnAccept = view.findViewById(R.id.btnAccept);
            btnReject = view.findViewById(R.id.btnReject);
            btnReSchedule = view.findViewById(R.id.btnReSchedule);

            btnAccept.setOnClickListener(this);
            btnReject.setOnClickListener(this);
            btnReSchedule.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull final View view) {
            switch (view.getId()) {

                case R.id.btnAccept:
                    if (getAdapterPosition() != -1 && clickListener != null)
                        clickListener.onAcceptClick(getAdapterPosition());
                    break;

                case R.id.btnReject:
                    if (getAdapterPosition() != -1 && clickListener != null)
                        clickListener.onRejectClick(getAdapterPosition());
                    break;

                case R.id.btnReSchedule:
                    if (getAdapterPosition() != -1 && clickListener != null)
                        clickListener.onRescheduleClick(getAdapterPosition());
                    break;

                default:
                    if (getAdapterPosition() != -1 && clickListener != null)
                        clickListener.onItemClick(getAdapterPosition());
                    break;
            }
        }
    }
}

