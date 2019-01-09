package com.mualab.org.biz.modules.add_staff.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.listner.EndlessRecyclerViewScrollListener;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.add_staff.AllArtist;
import com.mualab.org.biz.modules.add_staff.adapter.AllStaffAdapter;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SearchStaffFragment extends Fragment implements View.OnClickListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private Context mContext;
    private TextView tvNoDataFound;
    private List<AllArtist> artistStaffs;
    private AllStaffAdapter staffAdapter;
    private RecyclerView rvAllStaff;
    private EndlessRecyclerViewScrollListener scrollListener;
    private EditText etSearch;
    private ProgressBar progress_bar;

    public SearchStaffFragment() {
        // Required empty public constructor
    }


    public static SearchStaffFragment newInstance(String param1) {
        SearchStaffFragment fragment = new SearchStaffFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if (getArguments() != null) {
        //     mParam1 = getArguments().getString(ARG_PARAM1);
        // }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_staff, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View rootView){
        // if(mContext instanceof AddNewStaffActivity) {
        //   ((AddNewStaffActivity) mContext).setTitle();

        artistStaffs = new ArrayList<>();
        staffAdapter = new AllStaffAdapter(mContext, artistStaffs);

        rvAllStaff = rootView.findViewById(R.id.rvAllStaff);
        etSearch = rootView.findViewById(R.id.etSearch);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvAllStaff.setLayoutManager(layoutManager);
        rvAllStaff.setAdapter(staffAdapter);

        tvNoDataFound = rootView.findViewById(R.id.tvNoDataFound);
        progress_bar = rootView.findViewById(R.id.progress_bar);

        if(scrollListener==null) {
            scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                    staffAdapter.showLoading(true);
                    apiForGetAllStaff(page, "");
                    //apiForLoadMoreArtist(page);
                }
            };
        }

        // Adds the scroll listener to RecyclerView
        //  rvAllStaff.addOnScrollListener(scrollListener);


        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                artistStaffs.clear();
                Mualab.getInstance().cancelAllPendingRequests();
                String s = etSearch.getText().toString().trim();
                filterSearch(s);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input

            }
        });

        if(artistStaffs.size()==0) {
            progress_bar.setVisibility(View.VISIBLE);
            apiForGetAllStaff(0,"");
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void filterSearch(String s){
        if (!s.equals("")) {
            progress_bar.setVisibility(View.GONE);
            apiForGetAllStaff(0,s);
        }else {
            apiForGetAllStaff(0, "");
        }
    }

    private void apiForGetAllStaff(final int page, final String search){
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if(isConnected){
                        dialog.dismiss();
                        apiForGetAllStaff(page,search);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("search", search);
        //  params.put("page", ""+page);
        //  params.put("limit", "10");

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "allArtist", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");
                    progress_bar.setVisibility(View.GONE);

                    if (status.equalsIgnoreCase("success")) {
                        staffAdapter.showLoading(false);

                        if (page==0) {
                            artistStaffs.clear();
                        }

                        rvAllStaff.setVisibility(View.VISIBLE);
                        tvNoDataFound.setVisibility(View.GONE);

                        JSONArray jsonArray = js.getJSONArray("artistList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                AllArtist item = gson.fromJson(String.valueOf(object), AllArtist.class);

                                artistStaffs.add(item);
                            }
                        }else if (artistStaffs.size()==0 && page==0){
                            rvAllStaff.setVisibility(View.GONE);
                            tvNoDataFound.setVisibility(View.VISIBLE);
                        }
                        staffAdapter.notifyDataSetChanged();

                    }else {
                        rvAllStaff.setVisibility(View.GONE);
                        tvNoDataFound.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    Progress.hide(mContext);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

}
