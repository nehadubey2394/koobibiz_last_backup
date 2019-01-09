package com.mualab.org.biz.modules.booking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.modules.booking.adapter.StaffListAdapter;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.booking.listner.StaffSelectionListener;
import com.mualab.org.biz.session.Session;

import java.util.ArrayList;
import java.util.List;

public class StaffActivity extends AppCompatActivity implements StaffSelectionListener {
    private List<Staff> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        staffList = new ArrayList<>();

        Session session = Mualab.getInstance().getSessionManager();
        final User user = session.getUser();

        Staff item1 = new Staff();
        item1.staffName = "My Booking";
        item1.staffImage = user.profileImage;
        staffList.add(item1);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        List<Staff> tmpList = (ArrayList<Staff>) args.getSerializable("ARRAYLIST");

        if (tmpList != null) {
            staffList.addAll(tmpList);
        }

        // staffList = new ArrayList<>();
        StaffListAdapter staffListAdapter = new StaffListAdapter(StaffActivity.this, staffList);
        staffListAdapter.setStaffSelectionListner(StaffActivity.this);
        ImageView ivHeaderBack = findViewById(R.id.ivHeaderBack);
        ivHeaderBack.setVisibility(View.VISIBLE);
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(R.string.text_staff);

        TextView tvoData = findViewById(R.id.tvoData);
        RecyclerView rycvStaff = findViewById(R.id.rycvStaff);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(StaffActivity.this,3);
        rycvStaff.setItemAnimator(new DefaultItemAnimator());
        rycvStaff.setLayoutManager(linearLayoutManager);
        rycvStaff.setAdapter(staffListAdapter);

        AppCompatButton btnAllStaff = findViewById(R.id.btnAllStaff);


        if (staffList.size()==0){
            rycvStaff.setVisibility(View.GONE);
            tvoData.setVisibility(View.VISIBLE);
        }

        btnAllStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("staffId", "");
                intent.putExtra("staffName", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public void onStaffSelect(int position, String staffId) {
        String staffName = staffList.get(position).staffName;
        if (staffId==null){
            Session session = Mualab.getInstance().getSessionManager();
            User user = session.getUser();

           /* staffId = "0";
            staffName = "My Booking";*/
            // staffId = user.id;
            // staffName = "My Booking";
            staffId = "0";
            staffName = "My Booking";
        }
        Intent intent = new Intent();
        intent.putExtra("staffId", staffId);
        intent.putExtra("staffName", staffName);
        setResult(RESULT_OK, intent);
        finish();
    }
}
