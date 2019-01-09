package com.mualab.org.biz.modules.new_booking.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.model.booking.BookingTimeSlot;
import com.mualab.org.biz.modules.booking.listner.TimeSlotClickListener;

import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.ViewHolder> {

    private Context context;
    private List<BookingTimeSlot> itemList;
    private TimeSlotClickListener timeSlotClickListener;

    public TimeSlotAdapter(Context context, List<BookingTimeSlot> itemList, TimeSlotClickListener timeSlotClickListener) {
        this.context = context;
        this.itemList = itemList;
        this.timeSlotClickListener = timeSlotClickListener;
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeslot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int listPosition) {
        BookingTimeSlot bookingTimeSlot = itemList.get(listPosition);
        holder.tvTime.setText(bookingTimeSlot.time);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTime;
        RelativeLayout rlTimeSlot;

        ViewHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            rlTimeSlot = itemView.findViewById(R.id.rlTimeSlot);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            timeSlotClickListener.onButtonClick(getAdapterPosition(), "", 0);
        }
    }

}
