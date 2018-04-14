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
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.SubServices;

import java.util.ArrayList;


public class ArtistServiceLastAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<ArtistServices> artistsList;
    private SubServices subServices;
    // Constructor of the class
    public ArtistServiceLastAdapter(Context context, ArrayList<ArtistServices> artistsList, SubServices subServices) {
        this.context = context;
        this.artistsList = artistsList;
        this.subServices = subServices;
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
         /*   switch (view.getId()){
                case R.id.lyRemove:
                    if (fromConfirmBooking){
                        ArtistLastServicesFragment mServices3 = artistsList.get(getAdapterPosition());
                        mServices3.setBooked(false);
                        notifyDataSetChanged();

                        for (BookingInfo bookingInfo : BookingFragment4.arrayListbookingInfo) {
                            if (mServices3._id.equals(bookingInfo.msId)){
                                bookingId = bookingInfo.bookingId;
                                BookingFragment4.arrayListbookingInfo.remove(bookingInfo);
                                break;
                            }
                        }

                        apiForDeleteBookedService();
                    }
                    break;

                case R.id.lyServiceDetail:
                    if(isClicked){
                        return;
                    }
                    isClicked = true;

                    Session session = Mualab.getInstance().getSessionManager();
                    User user = session.getUser();
                    BookingServices3 services3 = artistsList.get(getAdapterPosition());
                    services3.setBooked(true);

                    BookingInfo bookingInfo = new BookingInfo();
                    //add data from artist main services
                    bookingInfo.artistService = services3.title;
                    bookingInfo.msId = services3._id;
                    bookingInfo.time = services3.completionTime;

                    //data from subservices
                    bookingInfo.sServiceName = subServices.subServiceName;
                    bookingInfo.ssId = subServices.subServiceId;
                    bookingInfo.sId = subServices.serviceId;
                    bookingInfo.subServices = subServices;
                    bookingInfo.isOutCallSelect = isOutCallSelect;
                    //       subServices.bookedArtistServices.addAll(artistsList);

                    //add data from services
                    bookingInfo.artistName = item.userName;
                    bookingInfo.profilePic = item.profileImage;
                    bookingInfo.artistId = item._id;
                    bookingInfo.artistAddress = item.address;
                    bookingInfo.item = item;
                    bookingInfo.userId = String.valueOf(user.id);

                    if (isOutCallSelect) {
                        bookingInfo.preperationTime = item.outCallpreprationTime;
                        bookingInfo.serviceType = "2";
                        bookingInfo.price = Double.parseDouble(services3.outCallPrice);
                    }else {
                        bookingInfo.price = Double.parseDouble(services3.inCallPrice);
                        bookingInfo.preperationTime = item.inCallpreprationTime;
                        bookingInfo.serviceType = "1";
                    }

                    int ctMinuts = 0,ptMinuts;

                    if (services3.completionTime.contains(":")){
                        String hours,min;
                        String[] separated = services3.completionTime.split(":");
                        hours = separated[0];
                        min = separated[1];
                        ctMinuts = utility.getTimeInMin(Integer.parseInt(hours),Integer.parseInt(min));
                    }

                    if (bookingInfo.preperationTime.contains(":")){
                        String hours,min;
                        String[] separated = bookingInfo.preperationTime.split(":");
                        hours = separated[0];
                        min = separated[1];
                        ptMinuts = utility.getTimeInMin(Integer.parseInt(hours),Integer.parseInt(min));

                        bookingInfo.serviceTime = "00:"+(ptMinuts+ctMinuts);
                        bookingInfo.endTime = ""+(ptMinuts+ctMinuts);
                        bookingInfo.editEndTime = ""+(ptMinuts+ctMinuts);

                    }

                    if (item.businessType.equals("independent")){
                        if (fromConfirmBooking){
                            ((BookingActivity)context).addFragment(
                                    BookingFragment4.newInstance(subServices.subServiceName,true,bookingInfo), true, R.id.flBookingContainer);
                        }else {
                            ((BookingActivity)context).addFragment(
                                    BookingFragment4.newInstance(subServices.subServiceName,false,bookingInfo), true, R.id.flBookingContainer);
                        }


                    }else {
                        ((BookingActivity)context).addFragment(
                                BookingFragment1.newInstance(serviceTitle,item, bookingInfo,fromConfirmBooking), true, R.id.flBookingContainer);

                    }
                    break;

            }*/
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