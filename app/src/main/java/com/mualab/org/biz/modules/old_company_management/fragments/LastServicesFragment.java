package com.mualab.org.biz.modules.old_company_management.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.SelectedServices;
import com.mualab.org.biz.model.add_staff.SubServices;
import com.mualab.org.biz.modules.add_staff.listner.OnServiceSelectListener;
import com.mualab.org.biz.modules.old_company_management.activity.CompanyServicesActivity;
import com.mualab.org.biz.modules.old_company_management.adapter.LastServiceAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class LastServicesFragment extends Fragment implements OnServiceSelectListener,
        View.OnClickListener
{
    private LastServiceAdapter adapter;
    private Context mContext;
    // TODO: Rename and change types of parameters
    private SubServices subServices;
    public static HashMap<String, SelectedServices> localMap = new HashMap<>();

    public LastServicesFragment() {
        // Required empty public constructor
    }

    public static LastServicesFragment newInstance(SubServices subServices) {
        LastServicesFragment fragment = new LastServicesFragment();
        Bundle args = new Bundle();
        args.putSerializable("param1", subServices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            subServices = (SubServices) getArguments().getSerializable("param1");
        }
        if(mContext instanceof CompanyServicesActivity) {
            ((CompanyServicesActivity)mContext).setTitle(subServices != null ? subServices.subServiceName : null);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist_last_service, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setViewId(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(){
        ArrayList<ArtistServices> arrayList = subServices.artistservices;
        adapter = new LastServiceAdapter(mContext, arrayList);

    }

    private void setViewId(View rootView){

        AppCompatButton btnAddMoreService = rootView.findViewById(R.id.btnAddMoreService);
        AppCompatButton btnDone = rootView.findViewById(R.id.btnDone);
        btnAddMoreService.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);

        RecyclerView rvLastService = rootView.findViewById(R.id.rvLastService);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvLastService.setLayoutManager(layoutManager);

        rvLastService.setAdapter(adapter);
        adapter.setListener(LastServicesFragment.this);
    }

    @Override
    public void onDestroyView() {
        Mualab.getInstance().cancelAllPendingRequests();
        ((CompanyServicesActivity)mContext).setTitle(getString(R.string.text_category));
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, ArtistServices artistServices) {
       // showDetailDialog(artistServices);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
        }
    }

}