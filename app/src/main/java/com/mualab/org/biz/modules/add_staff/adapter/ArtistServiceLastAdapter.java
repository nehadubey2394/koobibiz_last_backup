package com.mualab.org.biz.modules.add_staff.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.loopeer.shadow.ShadowView;
import com.mualab.org.biz.R;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.modules.add_staff.listner.OnServiceSelectListener;

import java.util.ArrayList;


public class ArtistServiceLastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ArtistServices> artistsList;
    private OnServiceSelectListener serviceSelectListener = null;
    // Constructor of the class
    public ArtistServiceLastAdapter(Context context, ArrayList<ArtistServices> artistsList) {
        this.context = context;
        this.artistsList = artistsList;
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

      /*  if (fromConfirmBooking){
            if (item.isBooked()) {
                holder.lyFrontView.setShadowColor(context.getResources().getColor(R.color.shadow_green));
                holder.sample1.setSwipeEnabled(true);
            }
            else {
                holder.sample1.setSwipeEnabled(false);
                holder.lyFrontView.setShadowColor(context.getResources().getColor(R.color.gray2));
            }
        }else {
            holder.sample1.setSwipeEnabled(false);
            holder.lyFrontView.setShadowColor(context.getResources().getColor(R.color.gray2));
        }*/
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

                    break;

                case R.id.lyServiceDetail:
                    ArtistServices artistServices = artistsList.get(getAdapterPosition()) ;
                    if (serviceSelectListener != null) {
                        serviceSelectListener.onItemClick(getAdapterPosition(),artistServices);
                    }
                    MyToast.getInstance(context).showDasuAlert("Under development");
                    break;
            }
        }
    }

/*
    private void apiForDeleteBookedService(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(context, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForDeleteBookedService();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        if (!bookingId.equals(""))
            params.put("bookingId", bookingId);
        // params.put("artistId", item._id);
        // params.put("userId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(context, "deleteBookService", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        if (BookingFragment4.arrayListbookingInfo.size()==0){
                            BookingFragment4.arrayListbookingInfo.clear();
                            ((BookingActivity)context).finish();
                        }else {
                            FragmentManager fm = ((BookingActivity)context).getSupportFragmentManager();
                            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            BookingInfo bookingInfo = BookingFragment4.arrayListbookingInfo.get(0);
                            ((BookingActivity)context).addFragment(
                                    BookingFragment5.newInstance(bookingInfo), true, R.id.flBookingContainer);
                        }


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
*/

}