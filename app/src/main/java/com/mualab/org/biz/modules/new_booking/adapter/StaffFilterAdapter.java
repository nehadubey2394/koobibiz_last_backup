package com.mualab.org.biz.modules.new_booking.adapter;

import android.support.annotation.NonNull;
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

public class StaffFilterAdapter extends RecyclerView.Adapter<StaffFilterAdapter.ViewHolder> {
    private static int lastPos = -1;
    private List<Staff> arrayList;
    private Listener listener;

    public StaffFilterAdapter(List<Staff> arrayList, Listener listener) {
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
        Staff bean = arrayList.get(position);
        holder.tvUserName.setText(bean.staffName);

        Picasso.with(holder.ivProfilePic.getContext()).load(bean.staffImage).placeholder(R.drawable.defoult_user_img).into(holder.ivProfilePic);

        if (bean.isSelected) {
            lastPos = holder.getAdapterPosition();
            holder.tvUserName.setTextColor(holder.tvUserName.getContext().getResources().getColor(R.color.colorPrimary));
        }
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
                int size = arrayList.size();
                if (lastPos != -1 && lastPos <= size) {
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
