package com.mualab.org.biz.modules.new_booking.adapter;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.add_staff.adapter.LoadingViewHolder;
import com.mualab.org.biz.modules.base.ItemClickListener;

import java.util.List;


/**
 * Created by hemant
 * Date: 17/4/18
 * Time: 4:03 PM
 */

public class ServiceAppointmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEWTYPE_LOADER = 2;
    private List<String> list;
    private boolean showLoader;
    private ItemClickListener clickListener;

    private int lastPos = -1;
    private boolean isClick = false;

    public ServiceAppointmentAdapter(List<String> list, ItemClickListener clickListener) {
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
        if (position == list.size() - 1) {
            return showLoader ? VIEWTYPE_LOADER : VIEWTYPE_ITEM;
        }
        return VIEWTYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        //return list.size();
        return 3;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        ViewHolder(@NonNull View view) {
            super(view);

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

