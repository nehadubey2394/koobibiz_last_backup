package com.mualab.org.biz.module.add_staff.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mualab.org.biz.R;


public class AddStaffFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;


    public AddStaffFragment() {
        // Required empty public constructor
    }


    public static AddStaffFragment newInstance(String param1, String param2) {
        AddStaffFragment fragment = new AddStaffFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_staff, container, false);
        initView(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initView(View rootView){
        RecyclerView rvAddStaff = rootView.findViewById(R.id.rvAddStaff);
    }

}
