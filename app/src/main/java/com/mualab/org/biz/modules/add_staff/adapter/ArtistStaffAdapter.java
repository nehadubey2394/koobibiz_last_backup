package com.mualab.org.biz.modules.add_staff.adapter;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AddedStaffServices;
import com.mualab.org.biz.model.add_staff.StaffDetail;
import com.mualab.org.biz.model.add_staff.StaffHours;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.add_staff.activity.AddStaffDetailActivity;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ArtistStaffAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Staff> staffList;

    // Constructor of the class
    public ArtistStaffAdapter(Context context,  List<Staff> staffList) {
        this.context = context;
        this.staffList = staffList;
    }

    @Override
    public int getItemCount() {
        return staffList.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_staff_item_layout, parent, false);
        return new ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final ViewHolder holder = ((ViewHolder) viewHolder);
        final Staff item = staffList.get(position);

        holder.tvStaffServices.setText(item.job);
        holder.tvStaffName.setText(item.staffName);

        if (!item.staffImage.equals("")){
            Picasso.with(context).load(item.staffImage).placeholder(R.drawable.defoult_user_img).
                    fit().into(holder.ivStaffProfile);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView tvStaffServices,tvStaffName;
        ImageView ivStaffProfile;
        private ViewHolder(View itemView)
        {
            super(itemView);

            ivStaffProfile = itemView.findViewById(R.id.ivStaffProfile);
            tvStaffServices = itemView.findViewById(R.id.tvStaffServices);
            tvStaffName = itemView.findViewById(R.id.tvStaffName);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Staff staff = staffList.get(getAdapterPosition());
            apiForGetStaffDetail(staff);
        }
    }

    private void apiForGetStaffDetail(final Staff staff){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(context, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetStaffDetail(staff);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", staff.staffId);
        params.put("businessId", String.valueOf(user.id));

        HttpTask task = new HttpTask(new HttpTask.Builder(context, "staffInformation", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        JSONArray jsonArray = js.getJSONArray("staffDetails");

                        if (jsonArray!=null && jsonArray.length()!=0) {
                            StaffDetail item = null;
                            for (int i=0; i<jsonArray.length(); i++){

                                item = new StaffDetail();
                                JSONObject object = jsonArray.getJSONObject(i);

                                item._id = object.getString("_id");
                                item.job = object.getString("job");
                                item.mediaAccess = object.getString("mediaAccess");
                                item.holiday = object.getString("holiday");

                                JSONObject staffInfo = object.getJSONObject("staffInfo");
                                item.userName = staffInfo.getString("userName");
                                item.profileImage = staffInfo.getString("profileImage");
                                item.staffId = staffInfo.getString("staffId");

                                JSONArray staffHoursArray = object.getJSONArray("staffHours");
                                for (int j=0; j<staffHoursArray.length(); j++){
                                    JSONObject object2 = staffHoursArray.getJSONObject(j);
                                    Gson gson = new Gson();
                                    StaffHours item2 = gson.fromJson(String.valueOf(object2), StaffHours.class);
                                    item.staffHoursList.add(item2);
                                }

                                JSONArray staffServiceArray = object.getJSONArray("staffService");
                                for (int k=0; k<staffServiceArray.length(); k++){
                                    JSONObject object3 = staffServiceArray.getJSONObject(k);
                                    Gson gson = new Gson();
                                    AddedStaffServices item3 = gson.fromJson(String.valueOf(object3), AddedStaffServices.class);
                                    item.staffServices.add(item3);
                                }
                            }
                            if (item!=null) {
                                Intent intent = new Intent(context, AddStaffDetailActivity.class);
                                Bundle args = new Bundle();
                                args.putSerializable("staff", item);
                                intent.putExtra("BUNDLE", args);
                                context.startActivity(intent);
                                // ((AddStaffActivity) context).finish();
                            }
                        }

                    }else {
                        MyToast.getInstance(context).showDasuAlert(message);
                    }
                    //  showToast(message);
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
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
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