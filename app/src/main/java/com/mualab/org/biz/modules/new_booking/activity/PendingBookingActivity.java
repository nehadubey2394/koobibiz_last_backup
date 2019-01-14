package com.mualab.org.biz.modules.new_booking.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.modules.new_booking.adapter.MyArrayAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.PendBookingsAdapter;
import com.mualab.org.biz.session.PreRegistrationSession;

import java.util.ArrayList;
import java.util.List;

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

        initSpinner();
        initPendBookingRecycler();
    }

    private void initSpinner() {

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        User user = Mualab.getInstance().getSessionManager().getUser();

        MyArrayAdapter bkTypeAdapter, bkStaffAdapter;

        //1:  Incall , 2: Outcall , 3: Both
        if (pSession.getServiceType() == 3) {
            Spinner spBkType = findViewById(R.id.spBkType);

            List<String> bkType = new ArrayList<>();
            bkType.add("All Type");
            bkType.add("Incall");
            bkType.add("Outcall");

            bkTypeAdapter = new MyArrayAdapter(getActivity(), bkType);
            spBkType.setAdapter(bkTypeAdapter);
        }

        if (!user.businessType.equals("independent")) {
            findViewById(R.id.rlStaff).setVisibility(View.VISIBLE);
            Spinner spBkStaff = findViewById(R.id.spBkStaff);
            //spBkStaff.setVisibility(View.VISIBLE);

            List<String> bkStaff = new ArrayList<>();
            bkStaff.add("All Staff");
            bkStaff.add("Staff 1");
            bkStaff.add("Staff 2");

            bkStaffAdapter = new MyArrayAdapter(getActivity(), bkStaff);
            spBkStaff.setAdapter(bkStaffAdapter);
        }
    }

    private void initPendBookingRecycler() {
        RecyclerView rvPendBookings = findViewById(R.id.rvPendBookings);

        List<String> list = new ArrayList<>();
        PendBookingsAdapter bookingsAdapter = new PendBookingsAdapter(list, new PendBookingsAdapter.ClickListener() {
            @Override
            public void onAcceptClick(int pos) {
                MyToast.getInstance(getBaseContext()).showSmallMessage(getString(R.string.under_development));
            }

            @Override
            public void onRejectClick(int pos) {
                MyToast.getInstance(getBaseContext()).showSmallMessage(getString(R.string.under_development));
            }

            @Override
            public void onRescheduleClick(int pos) {
                MyToast.getInstance(getBaseContext()).showSmallMessage(getString(R.string.under_development));
            }
        });
        rvPendBookings.setAdapter(bookingsAdapter);
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
