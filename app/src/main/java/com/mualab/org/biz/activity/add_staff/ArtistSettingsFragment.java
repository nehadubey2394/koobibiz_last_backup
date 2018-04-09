package com.mualab.org.biz.activity.add_staff;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mualab.org.biz.R;
import com.mualab.org.biz.activity.MainActivity;
import com.mualab.org.biz.application.Mualab;


public class ArtistSettingsFragment extends Fragment implements View.OnClickListener {
    private String mParam1;
    private Context mContext;


    public ArtistSettingsFragment() {
        // Required empty public constructor
    }


    public static ArtistSettingsFragment newInstance(String param1) {
        ArtistSettingsFragment fragment = new ArtistSettingsFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString("param1");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_settings, container, false);
        initView(rootView);

        setView(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(View rootView){
        ((MainActivity) mContext).setTitle(getString(R.string.title_buisness_admin));
    }

    private void setView(View rootView){
        RelativeLayout rlStaff = rootView.findViewById(R.id.rlStaff);
        RelativeLayout rlWorkinhHrs = rootView.findViewById(R.id.rlWorkinhHrs);
        RelativeLayout rlCategories = rootView.findViewById(R.id.rlCategories);
        RelativeLayout rlStaffPlanner = rootView.findViewById(R.id.rlStaffPlanner);
        RelativeLayout rlVoucherCode = rootView.findViewById(R.id.rlVoucherCode);

        rlStaff.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rlStaff:
                startActivity(new Intent(mContext,AddStaffActivity.class));
                break;
        }
    }
}
