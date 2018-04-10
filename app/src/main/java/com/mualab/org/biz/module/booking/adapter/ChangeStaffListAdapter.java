package com.mualab.org.biz.module.booking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.module.booking.listner.OnStaffChangeAndDoneClickListener;
import com.mualab.org.biz.model.booking.Staff;

import java.util.List;


public class ChangeStaffListAdapter extends RecyclerView.Adapter<ChangeStaffListAdapter.ViewHolder> {
    private Context context;
    private List<Staff> staffArrayList;
    private OnStaffChangeAndDoneClickListener listener = null;
    // Constructor of the class
    public ChangeStaffListAdapter(Context context, List<Staff> staffArrayList) {
        this.context = context;
        this.staffArrayList = staffArrayList;
    }

    public void setChangeListener(OnStaffChangeAndDoneClickListener listener){
        this.listener = listener;
    }
    // get the size of the list
    @Override
    public int getItemCount() {
            return staffArrayList.size();
    }


    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_staff_list, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {

        Staff staff =  staffArrayList.get(listPosition);
        holder.tvStaffName.setText(staff.staffName);

        if (staff.isSelected){
            holder.llChStaff.setBackgroundColor(context.getResources().getColor(R.color.gray2));
        }else {
            holder.llChStaff.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

    }

    // Static inner class to initialize the views of rows
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvStaffName;
        private LinearLayout llChStaff;
        private ViewHolder(View itemView)
        {
            super(itemView);
            itemView.setOnClickListener(this);
            tvStaffName =  itemView.findViewById(R.id.tvStaffName);
            llChStaff =  itemView.findViewById(R.id.llChStaff);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Staff staff = staffArrayList.get(getAdapterPosition());
            if (listener != null) {
                listener.onClick(getAdapterPosition(),staff);
            }
            for (Staff item : staffArrayList){
                item.isSelected = false;
            }

            staff.isSelected = true;
            notifyDataSetChanged();

        }
    }

}
