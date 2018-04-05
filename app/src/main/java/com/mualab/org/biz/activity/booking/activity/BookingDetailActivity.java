package com.mualab.org.biz.activity.booking.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.booking.adapter.BookedServicesAdapter;
import com.mualab.org.biz.activity.booking.adapter.ChangeStaffListAdapter;
import com.mualab.org.biz.activity.booking.listner.OnStaffChangeListener;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.BookingInfo;
import com.mualab.org.biz.model.booking.Bookings;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.model.booking.UserDetail;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDetailActivity extends AppCompatActivity implements OnStaffChangeListener,View.OnClickListener {
    private String bookingId;
    private BookedServicesAdapter adapter;
    private TextView tvBookingDate,tvBookingTime,tvBookingLoc,tvUserName;
    private List<BookingInfo> bookingInfoList;
    private ImageView ivChat,ivCall,ivLocation,ivCancle;
    private LinearLayout llBottom2,llBottom;
    private List<Staff> staffList;
    private Bookings item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);
        initView();
        setViewId();
    }

    private void initView(){
        Intent intent = getIntent();
        if (intent!=null){
            bookingId =  intent.getStringExtra("bookingId");
            Bundle args = intent.getBundleExtra("BUNDLE");
            staffList = (ArrayList<Staff>) args.getSerializable("ARRAYLIST");
        }else {
            staffList = new ArrayList<>();
        }
        item = new Bookings();
        bookingInfoList = new ArrayList<>();
        adapter = new BookedServicesAdapter(BookingDetailActivity.this, bookingInfoList);
        adapter.setListener(BookingDetailActivity.this);
    }

    private void setViewId(){
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);
        TextView  tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvBookingDate = findViewById(R.id.tvBookingDate);
        tvBookingTime = findViewById(R.id.tvBookingTime);
        tvBookingLoc = findViewById(R.id.tvBookingLoc);
        tvUserName = findViewById(R.id.tvUserName);

        ivChat = findViewById(R.id.ivChat);
        ivCall = findViewById(R.id.ivCall);
        ivLocation = findViewById(R.id.ivLocation);
        ivCancle = findViewById(R.id.ivCancle);

        ImageView ivCounter = findViewById(R.id.ivCounter);
        ImageView ivAccept = findViewById(R.id.ivAccept);
        ImageView ivReject = findViewById(R.id.ivReject);

        llBottom = findViewById(R.id.llBottom);
        llBottom2 = findViewById(R.id.llBottom2);

        RecyclerView rycServices = findViewById(R.id.rycServices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(BookingDetailActivity.this);
        rycServices.setLayoutManager(layoutManager);
        rycServices.setAdapter(adapter);

        apiForGetBookingDetail();

        ivAccept.setOnClickListener(this);
        ivReject.setOnClickListener(this);
        ivCounter.setOnClickListener(this);
    }

    @Override
    public void onStaffSelect(int position, BookingInfo bookingInfo) {
        if (staffList!=null)
            showStaffList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivAccept:
                if (item.bookStatus.equals("0")){
                    actionForBooking("accept");
                }
                break;
            case R.id.ivReject:
                actionForBooking("reject");
                break;
            case R.id.ivCounter:
                break;
        }
    }

    private void actionForBooking(String type){
        String serviceId = "",subServiceId = "",artistServiceId="";
        if (item.pendingBookingInfos.size()!=0){
            for (int i=0; i<item.pendingBookingInfos.size(); i++){
                BookingInfo bookingInfo = item.pendingBookingInfos.get(i);
                if (serviceId.equals("")){
                    serviceId = bookingInfo.serviceId;
                }else {
                    if (!serviceId.contains(bookingInfo.serviceId))
                        serviceId = serviceId + ","+bookingInfo.serviceId;
                }
                if (subServiceId.equals("")){
                    subServiceId = bookingInfo.subServiceId;
                }else {
                    if (!subServiceId.contains(bookingInfo.subServiceId))
                        subServiceId = subServiceId + ","+bookingInfo.subServiceId;
                }

                if (artistServiceId.equals("")){
                    artistServiceId = bookingInfo.artistServiceId;
                }else {
                    if (!artistServiceId.contains(bookingInfo.artistServiceId))
                        artistServiceId = artistServiceId + ","+bookingInfo.artistServiceId;
                }

            }
            apiForBookingAction(type,item,serviceId,subServiceId,artistServiceId);
        }

    }

    private void apiForGetBookingDetail(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(BookingDetailActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetBookingDetail();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);

        HttpTask task = new HttpTask(new HttpTask.Builder(BookingDetailActivity.this, "bookingDetails", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {

                        JSONArray array = js.getJSONArray("bookingDetails");
                        if (array!=null && array.length()!=0) {
                            for (int j = 0; j < array.length(); j++) {
                                String serviceName = "";

                                JSONObject object = array.getJSONObject(j);
                                item._id = object.getString("_id");
                                item.bookingDate = object.getString("bookingDate");
                                item.bookingTime = object.getString("bookingTime");
                                item.bookStatus = object.getString("bookStatus");
                                item.paymentType = object.getString("paymentType");
                                item.totalPrice = object.getString("totalPrice");
                                item.location = object.getString("location");

                                JSONArray arrUserDetail = object.getJSONArray("userDetail");
                                if (arrUserDetail != null && arrUserDetail.length() != 0) {
                                    for (int k = 0; k < arrUserDetail.length(); k++) {
                                        Gson gson = new Gson();
                                        JSONObject userObj = arrUserDetail.getJSONObject(k);
                                        item.userDetail = gson.fromJson(String.valueOf(userObj), UserDetail.class);
                                    }
                                }

                                JSONArray arrBookingInfo = object.getJSONArray("bookingInfo");
                                if (arrBookingInfo != null && arrBookingInfo.length() != 0) {
                                    for (int l = 0; l < arrBookingInfo.length(); l++) {
                                        JSONObject bInfoObj = arrBookingInfo.getJSONObject(l);
                                        BookingInfo bookingInfo = new BookingInfo();
                                        bookingInfo._Id = bInfoObj.getString("_id");
                                        bookingInfo.bookingPrice = bInfoObj.getString("bookingPrice");
                                        bookingInfo.serviceId = bInfoObj.getString("serviceId");
                                        bookingInfo.subServiceId = bInfoObj.getString("subServiceId");
                                        bookingInfo.artistServiceId = bInfoObj.getString("artistServiceId");
                                        bookingInfo.bookingDate = bInfoObj.getString("bookingDate");
                                        bookingInfo.startTime = bInfoObj.getString("startTime");
                                        bookingInfo.endTime = bInfoObj.getString("endTime");
                                        bookingInfo.staffId = bInfoObj.getString("staffId");
                                        bookingInfo.staffName = bInfoObj.getString("staffName");
                                        bookingInfo.staffImage = bInfoObj.getString("staffImage");
                                        bookingInfo.artistServiceName = bInfoObj.getString("artistServiceName");
                                        if (serviceName.equals("")) {
                                            serviceName = bookingInfo.artistServiceName;
                                        }
                                        bookingInfo.bookingDetail = item;

                                        if (item.bookStatus.equals("0")) {
                                            item.pendingBookingInfos.add(bookingInfo);
                                            bookingInfoList.add(bookingInfo);
                                        } else {
                                            item.todayBookingInfos.add(bookingInfo);
                                            bookingInfoList.add(bookingInfo);
                                        }
                                    }
                                    item.artistServiceName = serviceName;
                                    upDateUI(item,item.bookStatus);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }else {
                        MyToast.getInstance(BookingDetailActivity.this).showDasuAlert(message);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(BookingDetailActivity.this);
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

    private void upDateUI(Bookings bookingInfo,String status){
        tvBookingDate.setText(bookingInfo.bookingDate);
        tvBookingLoc.setText(bookingInfo.location);
        tvBookingTime.setText(bookingInfo.bookingDate);
        tvUserName.setText(bookingInfo.userDetail.userName);

        if (status.equals("0")){
            llBottom2.setVisibility(View.VISIBLE);
            llBottom.setVisibility(View.GONE);
        }else {
            llBottom2.setVisibility(View.GONE);
            llBottom.setVisibility(View.VISIBLE);
        }

        SimpleDateFormat dfInput = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfOutput = new SimpleDateFormat("d MMMM yyyy");
        Date formatedDate = null;
        try {
            formatedDate = dfInput.parse(bookingInfo.bookingDate);
            tvBookingDate.setText(dfOutput.format(formatedDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void showStaffList() {
        View DialogView = View.inflate(BookingDetailActivity.this, R.layout.dialog_layout_change_staff, null);

        final Dialog sheetDialog = new BottomSheetDialog(BookingDetailActivity.this);
        sheetDialog.setCanceledOnTouchOutside(true);
        sheetDialog.setCancelable(true);
        sheetDialog.setContentView(DialogView);

        TextView tvDone = DialogView.findViewById(R.id.tvDone);
        TextView tvCancle = DialogView.findViewById(R.id.tvCancle);
        TextView tvoData = DialogView.findViewById(R.id.tvoData);

        ChangeStaffListAdapter listAdapter = new ChangeStaffListAdapter(BookingDetailActivity.this, staffList);

        RecyclerView rycvStaff = DialogView.findViewById(R.id.rycvStaff);
        LinearLayoutManager layoutManager = new LinearLayoutManager(BookingDetailActivity.this);
        rycvStaff.setLayoutManager(layoutManager);
        rycvStaff.setAdapter(listAdapter);

        if (staffList.size()==0){
            tvoData.setVisibility(View.VISIBLE);
            rycvStaff.setVisibility(View.GONE);
        }

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetDialog.dismiss();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetDialog.dismiss();
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sheetDialog.dismiss();
            }
        });

        sheetDialog.show();
    }

    private void apiForBookingAction(final String type,final Bookings bookings,final String serviceId ,final String subServiceId ,final String artistServiceId){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(BookingDetailActivity.this, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForBookingAction(type,bookings,serviceId,subServiceId,artistServiceId);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("userId", bookings.userDetail._id);
        params.put("bookingId", bookings._id);
        params.put("serviceId", serviceId);
        params.put("subserviceId", subServiceId);
        params.put("artistServiceId", artistServiceId);
        params.put("type", type);


        HttpTask task = new HttpTask(new HttpTask.Builder(BookingDetailActivity.this, "bookingAction", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        MyToast.getInstance(BookingDetailActivity.this).showDasuAlert(message);
                    }else {
                        MyToast.getInstance(BookingDetailActivity.this).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(BookingDetailActivity.this);
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
