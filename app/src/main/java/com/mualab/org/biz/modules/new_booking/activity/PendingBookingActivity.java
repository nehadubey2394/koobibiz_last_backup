package com.mualab.org.biz.modules.new_booking.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.base.BaseActivity;

public class PendingBookingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_booking);
        initView();
    }

    private void initView() {
        /*Header update start here*/
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(R.string.pendingBookings);
        findViewById(R.id.ivHeaderBack).setOnClickListener(this);
                /*Header update End here*/
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
