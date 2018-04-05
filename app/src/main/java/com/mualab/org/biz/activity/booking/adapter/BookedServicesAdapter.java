package com.mualab.org.biz.activity.booking.adapter;


import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.booking.listner.OnStaffChangeListener;
import com.mualab.org.biz.model.booking.BookingInfo;

import java.util.List;


public class BookedServicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BookingInfo> artistsList;
    private OnStaffChangeListener listener = null;

    // Constructor of the class
    public BookedServicesAdapter(Context context, List<BookingInfo> artistsList) {
        this.context = context;
        this.artistsList = artistsList;
    }
    public void setListener(OnStaffChangeListener listener){
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_services, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final BookingInfo item = artistsList.get(position);

        holder.tvPrice.setText("£" + item.bookingPrice);
        holder.tvStaffName.setText(item.staffName);
        holder.tvServiceName.setText(item.artistServiceName);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvServiceName,tvAssignrdStaff,tvStaffName,tvPrice;
        AppCompatButton btnChangeStaff;
        private ViewHolder(View itemView)
        {
            super(itemView);

            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvAssignrdStaff = itemView.findViewById(R.id.tvAssignrdStaff);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnChangeStaff = itemView.findViewById(R.id.btnChangeStaff);

            btnChangeStaff.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btnChangeStaff:
                    BookingInfo bookingInfo = artistsList.get(getAdapterPosition());
                    if (listener != null) {
                        listener.onStaffSelect(getAdapterPosition(),bookingInfo);
                    }
                    break;
            }
        }

    }

}