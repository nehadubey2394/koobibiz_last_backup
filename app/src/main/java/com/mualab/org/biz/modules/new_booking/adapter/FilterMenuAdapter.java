package com.mualab.org.biz.modules.new_booking.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.new_booking.model.StaffFilter;

import java.util.ArrayList;

public class FilterMenuAdapter extends RecyclerView.Adapter<FilterMenuAdapter.ViewHolder> {
    private static int lastPos = -1;
    private ArrayList<StaffFilter> arrayList;
    private Listener listener;

    public FilterMenuAdapter(ArrayList<StaffFilter> arrayList, Listener listener) {
        this.arrayList = arrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StaffFilter bean = arrayList.get(position);
        holder.tvUserName.setText(bean.username);

        if (bean.isSelected)
            holder.tvUserName.setTextColor(holder.tvUserName.getContext().getResources().getColor(R.color.primary_blue));
        else
            holder.tvUserName.setTextColor(holder.tvUserName.getContext().getResources().getColor(R.color.text_color));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface Listener {
        void onMenuClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUserName;
        private ImageView ivProfilePic;

        public ViewHolder(View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() != -1) {
                if (lastPos != -1) {
                    arrayList.get(lastPos).isSelected = false;
                    notifyItemChanged(lastPos);
                }

                lastPos = getAdapterPosition();

                arrayList.get(lastPos).isSelected = true;
                notifyItemChanged(lastPos);

                listener.onMenuClick(getAdapterPosition());
            }
        }
    }
}
