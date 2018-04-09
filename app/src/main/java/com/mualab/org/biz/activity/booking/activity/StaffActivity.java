package com.mualab.org.biz.activity.booking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.booking.adapter.StaffListAdapter;
import com.mualab.org.biz.model.booking.Staff;

import java.util.ArrayList;
import java.util.List;

public class StaffActivity extends AppCompatActivity {
    private StaffListAdapter staffListAdapter;
    private List<Staff>staffList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        staffList = (ArrayList<Staff>) args.getSerializable("ARRAYLIST");

        // staffList = new ArrayList<>();
        staffListAdapter = new StaffListAdapter(StaffActivity.this,staffList);

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

        if (staffList.size()==0){
            rycvStaff.setVisibility(View.GONE);
            tvoData.setVisibility(View.VISIBLE);
        }
        //  showStaff();

        ivHeaderBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showStaff(){
        staffList.clear();
        for (int i = 0;i<12;i++){
            Staff staff = new Staff();
            staff.staffName = "Benjamin";
            staffList.add(staff);
        }
        staffListAdapter.notifyDataSetChanged();
    }
}
