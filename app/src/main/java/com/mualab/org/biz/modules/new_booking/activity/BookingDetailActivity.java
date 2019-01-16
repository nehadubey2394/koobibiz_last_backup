package com.mualab.org.biz.modules.new_booking.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.modules.new_booking.adapter.ServiceAppointmentAdapter;

import java.util.ArrayList;

public class BookingDetailActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking_detail);

        initView();
    }

    private void initView() {
          /*Header update start here*/
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(R.string.appoinment);
        findViewById(R.id.ivHeaderBack).setOnClickListener(this);
        /*Header update End here*/

        RecyclerView rvServices = findViewById(R.id.rvServices);
        ServiceAppointmentAdapter appointmentAdapter = new ServiceAppointmentAdapter(new ArrayList<>(), pos -> {

        });
        rvServices.setAdapter(appointmentAdapter);
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
