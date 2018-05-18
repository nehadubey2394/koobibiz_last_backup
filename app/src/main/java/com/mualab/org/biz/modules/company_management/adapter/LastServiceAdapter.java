package com.mualab.org.biz.modules.company_management.adapter;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.daimajia.swipe.SwipeLayout;
import com.loopeer.shadow.ShadowView;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AddedStaffServices;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.add_staff.activity.AllServicesActivity;
import com.mualab.org.biz.modules.add_staff.listner.OnServiceSelectListener;
import com.mualab.org.biz.modules.company_management.activity.CompanyServicesActivity;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mualab.org.biz.modules.add_staff.fragments.ArtistLastServicesFragment.localMap;


public class LastServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ArtistServices> artistsList;
    private OnServiceSelectListener serviceSelectListener = null;
    private CompanyDetail staffDetail;
    // Constructor of the class
    public LastServiceAdapter(Context context, ArrayList<ArtistServices> artistsList) {
        this.context = context;
        this.artistsList = artistsList;
        this.staffDetail = ((CompanyServicesActivity)context).getCompanyDetail();
    }

    public void setListener(OnServiceSelectListener listener){
        this.serviceSelectListener = listener;
    }

    @Override
    public int getItemCount() {
        return artistsList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_last_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final ArtistServices item = artistsList.get(position);
        holder.tvLastService.setText(item.title);

        String totalTime;
        if (!item.editedCtime.isEmpty()){
            totalTime = item.editedCtime;
        }else
            totalTime = item.completionTime;

        if (totalTime.contains(":")){
            String[] separated = totalTime.split(":");
            String hours = separated[0]+" hr ";
            String min = separated[1]+" min";

            if (hours.equals("00 hr "))
                holder.tvCtime.setText(min);
            else if (!hours.equals("00 hr ") && min.equals("00 min"))
                holder.tvCtime.setText(hours);
            else
                holder.tvCtime.setText(hours+min);

        }
        double inCallPrice = 0.0,outCallPrice = 0.0;
        if (!item.editedInCallP.isEmpty()){
            inCallPrice = Double.parseDouble(item.editedInCallP);
        }else
            inCallPrice = Double.parseDouble(item.inCallPrice);

        if (!item.editedOutCallP.isEmpty()){
            outCallPrice = Double.parseDouble(item.editedOutCallP);
        }else
            outCallPrice = Double.parseDouble(item.outCallPrice);

        holder.tvOutCallPrice.setText("£"+String.format("%.2f", outCallPrice));
        holder.tvInCallPrice.setText("£"+String.format("%.2f", inCallPrice));

        holder.lyFrontView.setShadowColor(context.getResources().getColor(R.color.shadow_green));
        holder.sample1.setSwipeEnabled(false);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvCtime,tvLastService,tvOutCallPrice,tvInCallPrice;
        LinearLayout lyServiceDetail,lyRemove;
        SwipeLayout sample1;
        ShadowView lyFrontView;
        private ViewHolder(View itemView)
        {
            super(itemView);
            tvCtime = itemView.findViewById(R.id.tvCtime);
            lyServiceDetail = itemView.findViewById(R.id.lyServiceDetail);
            lyRemove = itemView.findViewById(R.id.lyRemove);
            tvLastService = itemView.findViewById(R.id.tvLastService);
            tvInCallPrice = itemView.findViewById(R.id.tvInCallPrice);
            tvOutCallPrice = itemView.findViewById(R.id.tvOutCallPrice);
            sample1 = itemView.findViewById(R.id.sample1);
            lyFrontView = itemView.findViewById(R.id.lyFrontView);
            lyFrontView.setShadowDy(context.getResources().getDimension(R.dimen.shadow_width));
            lyFrontView.setShadowDx(context.getResources().getDimension(R.dimen.shadow_width));

            lyServiceDetail.setOnClickListener(this);
            lyRemove.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.lyServiceDetail:
                    ArtistServices artistServices = artistsList.get(getAdapterPosition()) ;
                    if (serviceSelectListener != null) {
                        serviceSelectListener.onItemClick(getAdapterPosition(),artistServices);
                    }
                    break;
            }
        }
    }
}