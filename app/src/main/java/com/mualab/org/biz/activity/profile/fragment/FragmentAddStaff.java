package com.mualab.org.biz.activity.profile.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;

/**
 * Created by dharmraj on 2/2/18.
 **/

public class FragmentAddStaff extends ProfileCreationBaseFragment{

    public static FragmentAddStaff newInstance() {
        FragmentAddStaff fragment = new FragmentAddStaff();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        //View view = inflater.inflate(R.layout.fragment_add_staff, container, false);
        //return view;
        return inflater.inflate(R.layout.fragment_add_staff, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mualab.getInstance().getSessionManager().logout();
            }
        });

        view.findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFinish();
            }
        });
    }


}
