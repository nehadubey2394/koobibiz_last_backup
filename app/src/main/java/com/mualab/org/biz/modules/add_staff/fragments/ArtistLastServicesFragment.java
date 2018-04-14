package com.mualab.org.biz.modules.add_staff.fragments;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.EditText;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.model.add_staff.ArtistServices;
import com.mualab.org.biz.model.add_staff.SubServices;
import com.mualab.org.biz.modules.add_staff.activity.AllServicesActivity;
import com.mualab.org.biz.modules.add_staff.adapter.ArtistServiceLastAdapter;
import com.mualab.org.biz.modules.add_staff.listner.OnServiceSelectListener;

import java.util.ArrayList;


public class ArtistLastServicesFragment extends Fragment implements OnServiceSelectListener {
    private ArtistServiceLastAdapter adapter;
    private Context mContext;
    // TODO: Rename and change types of parameters
    private SubServices subServices;

    public ArtistLastServicesFragment() {
        // Required empty public constructor
    }

    public static ArtistLastServicesFragment newInstance(SubServices subServices) {
        ArtistLastServicesFragment fragment = new ArtistLastServicesFragment();
        Bundle args = new Bundle();
        args.putSerializable("param1", subServices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if(mContext instanceof BookingActivity) {
            ((AllServicesActivity) mContext).setReviewPostVisibility(8);
        }*/

        if (getArguments() != null) {
            subServices = (SubServices) getArguments().getSerializable("param1");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_last_service, container, false);
        initView();
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewId(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(){

        ArrayList<ArtistServices> arrayList;

        arrayList = subServices.artistservices;

        adapter = new ArtistServiceLastAdapter(mContext, arrayList);

    }

    private void setViewId(View rootView){
        if(mContext instanceof AllServicesActivity) {
          //  StaffDetail staffDetail = ((AllServicesActivity)mContext).getStaffDetail();
            ((AllServicesActivity)mContext).setTitle(subServices.subServiceName);
        }

        RecyclerView rvLastService = rootView.findViewById(R.id.rvLastService);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvLastService.setLayoutManager(layoutManager);

        rvLastService.setAdapter(adapter);
        adapter.setListener(ArtistLastServicesFragment.this);
    }

    @Override
    public void onDestroyView() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroy();
    }

    @Override
    public void onItemClick(int position, ArtistServices artistServices) {
        showDetailDialog(artistServices);
    }

    private void showDetailDialog(ArtistServices artistServices){
        View DialogView = View.inflate(mContext, R.layout.dialog_layout_service_detail, null);


        final Dialog dialog = new Dialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(DialogView);
        EditText etInCallPrice = DialogView.findViewById(R.id.etInCallPrice);
        EditText etOutCallPrice = DialogView.findViewById(R.id.etOutCallPrice);
        TextView tvCTime = DialogView.findViewById(R.id.tvCTime);
        TextView tvTile = DialogView.findViewById(R.id.tvTile);
        AppCompatButton btnDone = DialogView.findViewById(R.id.btnDone);
        AppCompatButton btnCancel = DialogView.findViewById(R.id.btnCancel);

        tvTile.setText(artistServices.title);
        etInCallPrice.setText(artistServices.inCallPrice);
        etOutCallPrice.setText(artistServices.outCallPrice);
        tvCTime.setText(artistServices.completionTime);
        etInCallPrice.clearFocus();
        etOutCallPrice.clearFocus();

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
}