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
import com.mualab.org.biz.modules.booking.adapter.StaffListAdapter;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.booking.listner.StaffSelectionListener;

import java.util.ArrayList;
import java.util.List;

public class StaffActivity extends AppCompatActivity implements StaffSelectionListener {
    private List<Staff> staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        staffList = (ArrayList<Staff>) args.getSerializable("ARRAYLIST");

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

        AppCompatButton btnNewAppointment = findViewById(R.id.btnNewAppointment);


        if (staffList.size()==0){
            rycvStaff.setVisibility(View.GONE);
            tvoData.setVisibility(View.VISIBLE);
        }

        btnNewAppointment.setOnClickListener(new View.OnClickListener() {
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
        Intent intent = new Intent();
        intent.putExtra("staffId", staffId);
        intent.putExtra("staffName", staffList.get(position).staffName);
        setResult(RESULT_OK, intent);
        finish();
    }
}
