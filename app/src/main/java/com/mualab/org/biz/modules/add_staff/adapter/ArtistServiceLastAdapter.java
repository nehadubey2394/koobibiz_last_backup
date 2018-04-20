package com.mualab.org.biz.modules.add_staff.adapter;


import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
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
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.modules.add_staff.activity.AllServicesActivity;
import com.mualab.org.biz.modules.add_staff.fragments.ArtistLastServicesFragment;
import com.mualab.org.biz.modules.add_staff.listner.OnServiceSelectListener;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ArtistServiceLastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ArtistServices> artistsList;
    private OnServiceSelectListener serviceSelectListener = null;
    private StaffDetail staffDetail;
    // Constructor of the class
    public ArtistServiceLastAdapter(Context context, ArrayList<ArtistServices> artistsList) {
        this.context = context;
        this.artistsList = artistsList;
        this.staffDetail = ((AllServicesActivity)context).getStaffDetail();
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

        String totalTime = item.completionTime;
        if (totalTime.contains(":")){
            String[] separated = totalTime.split(":");
            String hours = separated[0]+" hrs ";
            String min = separated[1]+" min";

            if (hours.equals("00 hrs "))
                holder.tvCtime.setText(min);
            else if (!hours.equals("00 hrs ") && min.equals("00 min"))
                holder.tvCtime.setText(hours);
            else
                holder.tvCtime.setText(hours+min);

        }
        double inCallPrice = 0.0,outCallPrice = 0.0;
        outCallPrice = Double.parseDouble(item.outCallPrice);
        inCallPrice = Double.parseDouble(item.inCallPrice);

        holder.tvOutCallPrice.setText("£"+outCallPrice);
        holder.tvInCallPrice.setText("£"+inCallPrice);

        if (item.isSelected()) {
            holder.lyFrontView.setShadowColor(context.getResources().getColor(R.color.shadow_green));
            holder.sample1.setSwipeEnabled(true);
        }
        else {
            holder.sample1.setSwipeEnabled(false);
            holder.lyFrontView.setShadowColor(context.getResources().getColor(R.color.gray2));
        }
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
                case R.id.lyRemove:
                    ArtistServices mServices3 = artistsList.get(getAdapterPosition());
                    mServices3.setSelected(false);
                    notifyDataSetChanged();

                    for (SelectedServices selectedServices : ArtistLastServicesFragment.selectedServicesList) {
                        if (mServices3._id.equals(selectedServices.artistServiceId)){
                            if (selectedServices._id.equals("#"))
                                ArtistLastServicesFragment.selectedServicesList.remove(selectedServices);
                            else
                                apiForDeleteBookedService(selectedServices);
                            break;
                        }
                    }

                    break;

                case R.id.lyServiceDetail:
                    ArtistServices artistServices = artistsList.get(getAdapterPosition()) ;
                    if (serviceSelectListener != null) {
                        serviceSelectListener.onItemClick(getAdapterPosition(),artistServices);
                    }
                    break;
            }
        }
    }

    private void apiForDeleteBookedService(final SelectedServices selectedServices){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(context, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteBookedService(selectedServices);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("addServiceId", selectedServices._id);
        // params.put("userId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(context, "deleteStaffService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        if (ArtistLastServicesFragment.selectedServicesList.size()==0 ||
                                ArtistLastServicesFragment.selectedServicesList.size()==1){
                            ArtistLastServicesFragment.selectedServicesList.clear();
                            staffDetail.staffServices.clear();
                            ((AllServicesActivity)context).finish();

                        } /*else if (ArtistLastServicesFragment.selectedServicesList.size()==1){
                            ArtistLastServicesFragment.selectedServicesList.remove(selectedServices);
                            FragmentManager fm = ((AllServicesActivity)context).getSupportFragmentManager();
                            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        }*/else{
                            ArtistLastServicesFragment.selectedServicesList.remove(selectedServices);
                        }
                        MyToast.getInstance(context).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(context).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(context);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showSmallCustomToast(helper.error_Messages(error));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

}