package com.mualab.org.biz.activity.profile.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;

import java.util.HashMap;
import java.util.Map;

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
                //Mualab.getInstance().getSessionManager().logout();
                showToast(R.string.under_development);
            }
        });

        view.findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completProfile();
            }
        });
    }


    private void completProfile(){
        Map<String,String> body = new HashMap<>();
        body.put("artistId", ""+user.id);
        new HttpTask(new HttpTask.Builder(mContext, "skipPage", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                listener.onFinish();
            }

            @Override
            public void ErrorListener(VolleyError error) {

            }
        }).setMethod(Request.Method.POST)
                .setBodyContentType( HttpTask.ContentType.APPLICATION_JSON)
                .setBody(body)
                .setProgress(true)
                .setAuthToken(user.authToken)).execute("skip");
    }
}
