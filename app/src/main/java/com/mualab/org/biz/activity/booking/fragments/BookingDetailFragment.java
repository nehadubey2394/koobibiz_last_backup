package com.mualab.org.biz.activity.booking.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.MainActivity;
import com.mualab.org.biz.activity.booking.adapter.BookedServicesAdapter;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.BookingInfo;
import com.mualab.org.biz.model.booking.Bookings;
import com.mualab.org.biz.model.booking.UserDetail;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookingDetailFragment extends Fragment {
    // TODO: Rename and change types of parameters
    private String bookingId;
    private BookedServicesAdapter adapter;
    private Context mContext;
    private TextView tvBookingDate,tvBookingTime,tvBookingLoc;
    private List<BookingInfo> bookingInfoList;
    private ImageView ivChat,ivCall,ivLocation,ivCancle;
    private LinearLayout llBottom2,llBottom;

    public BookingDetailFragment() {
        // Required empty public constructor
    }


    public static BookingDetailFragment newInstance(String param1) {
        BookingDetailFragment fragment = new BookingDetailFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingId =  getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_booking_detail, container, false);

        initView();
        setViewId(rootView);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiForGetBookingDetail();
    }

    private void initView(){
        bookingInfoList = new ArrayList<>();
        adapter = new BookedServicesAdapter(mContext, bookingInfoList);
    }

    private void setViewId(View rootView){
        if(mContext instanceof MainActivity) {
            ((MainActivity) mContext).setTitle(getString(R.string.appoinment));
            ((MainActivity) mContext).setBackButtonVisibility(0);
        }
        tvBookingDate = rootView.findViewById(R.id.tvBookingDate);
        tvBookingTime = rootView.findViewById(R.id.tvBookingTime);
        tvBookingLoc = rootView.findViewById(R.id.tvBookingLoc);

        ivChat = rootView.findViewById(R.id.ivChat);
        ivCall = rootView.findViewById(R.id.ivCall);
        ivLocation = rootView.findViewById(R.id.ivLocation);
        ivCancle = rootView.findViewById(R.id.ivCancle);

        llBottom = rootView.findViewById(R.id.llBottom);
        llBottom2 = rootView.findViewById(R.id.llBottom2);

        RecyclerView rycServices = rootView.findViewById(R.id.rycServices);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rycServices.setLayoutManager(layoutManager);
        rycServices.setAdapter(adapter);
    }

    private void apiForGetBookingDetail(){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
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

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "bookingDetails", new HttpResponceListner.Listener() {
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
                                Bookings item = new Bookings();
                                item._id = object.getString("_id");
                                item.bookingDate = object.getString("bookingDate");
                                item.bookingTime = object.getString("bookingTime");
                                item.bookStatus = object.getString("bookStatus");
                                item.paymentType = object.getString("paymentType");
                                item.totalPrice = object.getString("totalPrice");

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
                                        bookingInfo.bookingStatus = item.bookStatus;

                                        bookingInfo.userDetail = item.userDetail;

                                        upDateUI(bookingInfo,item.bookStatus);

                                        if (item.bookStatus.equals("0")) {
                                            item.pendingBookingInfos.add(bookingInfo);
                                            bookingInfoList.add(bookingInfo);
                                        } else {
                                            item.todayBookingInfos.add(bookingInfo);
                                            bookingInfoList.add(bookingInfo);
                                        }
                                    }
                                    item.artistServiceName = serviceName;
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }

                    }else {
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(mContext);
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

    private void upDateUI(BookingInfo bookingInfo,String status){
        tvBookingDate.setText(bookingInfo.bookingDate);
        tvBookingLoc.setText("NA");
        tvBookingTime.setText(bookingInfo.startTime);
        if (status.equals("0")){
            llBottom2.setVisibility(View.VISIBLE);
            llBottom.setVisibility(View.GONE);
        }else {
            llBottom2.setVisibility(View.GONE);
            llBottom.setVisibility(View.VISIBLE);
        }
    }

}
