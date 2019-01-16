package com.mualab.org.biz.modules.new_booking.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.modules.new_booking.adapter.ServiceAppointmentAdapter;
import com.mualab.org.biz.modules.new_booking.model.BookingDetail;
import com.mualab.org.biz.util.AppLogger;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class BookingDetailActivity extends BaseActivity implements View.OnClickListener {

    private ServiceAppointmentAdapter appointmentAdapter;
    private String bookingId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking_detail);

        bookingId = getIntent().getStringExtra(Constants.BOOKING_ID);
        initView();
    }

    private void initView() {
          /*Header update start here*/
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(R.string.appoinment);
        findViewById(R.id.ivHeaderBack).setOnClickListener(this);
        /*Header update End here*/

        RecyclerView rvServices = findViewById(R.id.rvServices);
        appointmentAdapter = new ServiceAppointmentAdapter(new ArrayList<>(), pos -> {

        });
        rvServices.setAdapter(appointmentAdapter);

        doGetBookingDetail();
    }

    private void doGetBookingDetail() {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(getActivity(), (dialog, isConnected) -> {
                if (isConnected) {
                    dialog.dismiss();
                    doGetBookingDetail();
                }
            }).show();
        }

        setLoading(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("authToken", Mualab.getInstance().getSessionManager().getUser().authToken);

        HashMap<String, String> params = new HashMap<>();
        params.put("bookingId", bookingId);

        getDataManager().doGetBookingDetail(header, params).getAsString(new StringRequestListener() {

            @Override
            public void onResponse(String response) {
                AppLogger.e("onResponse", response);
                setLoading(false);
                BookingDetail bookingDetail = getDataManager().getGson().fromJson(response, BookingDetail.class);

                if (bookingDetail.getStatus().equalsIgnoreCase("success")) {
                    updateUI(bookingDetail);
                } else {
                    MyToast.getInstance(getActivity()).showSmallMessage(bookingDetail.getMessage());
                }
            }

            @Override
            public void onError(ANError anError) {
                setLoading(false);
                Helper helper = new Helper();
                helper.parseError(anError.getErrorBody());
            }
        });
    }

    private void updateUI(BookingDetail bookingDetail) {
        ImageView ivProfilePic = findViewById(R.id.ivProfilePic);
        TextView tvUserName = findViewById(R.id.tvUserName);
        TextView tvStatus = findViewById(R.id.tvStatus);
        TextView tvCallType = findViewById(R.id.tvCallType);
        TextView tvAddress = findViewById(R.id.tvAddress);
        TextView tvVoucherCode = findViewById(R.id.tvVoucherCode);
        TextView tvPaymentMode = findViewById(R.id.tvPaymentMode);
        TextView tvAmt = findViewById(R.id.tvAmt);

        Picasso.with(getActivity()).load(bookingDetail.getData().getUserDetail().get(0).getProfileImage()).placeholder(R.drawable.defoult_user_img).into(ivProfilePic);
        tvUserName.setText(bookingDetail.getData().getUserDetail().get(0).getUserName());
        tvStatus.setText(getBookingStatus(tvStatus, bookingDetail.getData().getBookStatus()));
        tvCallType.setText(bookingDetail.getData().getBookingType().equals("1") ? getString(R.string.in_call) : getString(R.string.out_call));
        tvAddress.setText(bookingDetail.getData().getLocation());
        tvVoucherCode.setText(bookingDetail.getData().getVoucher());
        tvPaymentMode.setText(bookingDetail.getData().getPaymentType() == 1 ? getString(R.string.onlinePayment) : getString(R.string.offlinePayment));
        tvAmt.setText(bookingDetail.getData().getTotalPrice());
    }

    private String getBookingStatus(TextView tvStatus, String bookStatus) {
        //0 - pending, 1- accept, 2 - reject or cancel,3 - complete
        String status = "";
        switch (bookStatus) {
            case "0":
                status = "Pending";
                tvStatus.setTextColor(getResources().getColor(R.color.colorOrange));
                break;

            case "1":
                status = "Confirmed";
                tvStatus.setTextColor(getResources().getColor(R.color.text_color_green));
                break;

            case "2":
                status = "Cancelled";
                tvStatus.setTextColor(getResources().getColor(R.color.primary_red));
                break;

            case "3":
                status = "Completed";
                tvStatus.setTextColor(getResources().getColor(R.color.text_color_green));
                break;
        }
        return status;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeaderBack:
                onBackPressed();
                break;
        }
    }
}
