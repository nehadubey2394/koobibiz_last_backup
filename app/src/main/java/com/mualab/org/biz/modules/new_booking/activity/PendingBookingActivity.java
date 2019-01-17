package com.mualab.org.biz.modules.new_booking.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.modules.base.BaseActivity;
import com.mualab.org.biz.modules.new_booking.adapter.CallTypeArrayAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.PendBookingsAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.StaffArrayAdapter;
import com.mualab.org.biz.modules.new_booking.model.BookingFilterModel;
import com.mualab.org.biz.modules.new_booking.model.BookingHistory;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.AppLogger;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingBookingActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = PendingBookingActivity.class.getName();
    private Spinner spBkType, spBkStaff;
    private TextView tvNoRecord;

    private User user;
    private PendBookingsAdapter pendBookingsAdapter;
    private StaffArrayAdapter bkStaffAdapter;
    private CallTypeArrayAdapter bkTypeAdapter;
    private List<Staff> artistStaffList;
    private List<BookingHistory.DataBean> pendingBookingHistory;
    private List<BookingFilterModel> bkTypeList;
    private Boolean isOnItemSelectedRun = true;
    private String sCurCompId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_booking);
        user = Mualab.getInstance().getSessionManager().getUser();
        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        sCurCompId = pSession.getCurrentCompanyDetail()._id;
        initView();
    }

    private void initView() {
        /*Header update start here*/
        TextView tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderTitle.setText(R.string.pendingBookings);
        findViewById(R.id.ivHeaderBack).setOnClickListener(this);
        /*Header update End here*/

        tvNoRecord = findViewById(R.id.tvNoRecord);
        initPendBookingRecycler();
        initSpinner();
    }

    private void initSpinner() {

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        User user = Mualab.getInstance().getSessionManager().getUser();

        //1:  Incall , 2: Outcall , 3: Both
        if (pSession.getServiceType() == 3) {
            spBkType = findViewById(R.id.spBkType);

            bkTypeList = new ArrayList<>();
            BookingFilterModel filterModel = new BookingFilterModel();
            filterModel.displayName = "All Type";
            filterModel.name = "";
            bkTypeList.add(filterModel);

            filterModel = new BookingFilterModel();
            filterModel.displayName = "Incall";
            filterModel.name = "1";
            bkTypeList.add(filterModel);

            filterModel = new BookingFilterModel();
            filterModel.displayName = "Outcall";
            filterModel.name = "2";
            bkTypeList.add(filterModel);

            bkTypeAdapter = new CallTypeArrayAdapter(getActivity(), bkTypeList);
            spBkType.setAdapter(bkTypeAdapter);
            spBkType.setOnItemSelectedListener(this);
        }

        if (!user.businessType.equals("independent")) {
            spBkStaff = findViewById(R.id.spBkStaff);
            //spBkStaff.setVisibility(View.VISIBLE);
            artistStaffList = new ArrayList<>();
            apiForGetArtistStaff();
        }
    }

    private Staff getDefaultStaff() {
        Staff staff = new Staff();
        staff.staffName = "All Staff";
        staff.staffId = "";
        return staff;
    }

    private void initPendBookingRecycler() {
        RecyclerView rvPendBookings = findViewById(R.id.rvPendBookings);

        pendingBookingHistory = new ArrayList<>();
        pendBookingsAdapter = new PendBookingsAdapter(pendingBookingHistory, new PendBookingsAdapter.ClickListener() {
            @Override
            public void onAcceptClick(int pos) {
                MyToast.getInstance(getActivity()).showSmallMessage(getString(R.string.under_development));
            }

            @Override
            public void onRejectClick(int pos) {
                MyToast.getInstance(getActivity()).showSmallMessage(getString(R.string.under_development));
            }

            @Override
            public void onRescheduleClick(int pos) {
                MyToast.getInstance(getActivity()).showSmallMessage(getString(R.string.under_development));
            }
        });
        rvPendBookings.setAdapter(pendBookingsAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivHeaderBack:
                onBackPressed();
                break;
        }
    }

    private void apiForGetArtistStaff() {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(getActivity(), (dialog, isConnected) -> {
                if (isConnected) {
                    dialog.dismiss();
                    apiForGetArtistStaff();
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", sCurCompId.isEmpty() ? String.valueOf(user.id) : sCurCompId);
        params.put("search", "");

        HttpTask task = new HttpTask(new HttpTask.Builder(getActivity(), "artistStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        artistStaffList.clear();
                        artistStaffList.add(getDefaultStaff());
                        JSONArray jsonArray = js.getJSONArray("staffList");
                        if (jsonArray != null && jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                // Staff item = new Staff();
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                Staff item = gson.fromJson(String.valueOf(object), Staff.class);
                                artistStaffList.add(item);
                            }
                            updateStaffFilterUI();
                        } else {
                            updateStaffFilterUI();
                        }

                    } else {
                        updateStaffFilterUI();
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    updateStaffFilterUI();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        // MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(TAG);
    }

    private void updateStaffFilterUI() {
        findViewById(R.id.rlStaff).setVisibility(View.VISIBLE);
        if (artistStaffList.isEmpty()) artistStaffList.add(getDefaultStaff());
        bkStaffAdapter = new StaffArrayAdapter(getActivity(), artistStaffList);
        spBkStaff.setAdapter(bkStaffAdapter);
        spBkStaff.setOnItemSelectedListener(this);
    }

    private void doGetPendingBookingHistory(String staffId, String bookingType) {

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(getActivity(), (dialog, isConnected) -> {
                if (isConnected) {
                    dialog.dismiss();
                    doGetPendingBookingHistory(staffId, bookingType);
                }
            }).show();
        }

        setLoading(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("authToken", Mualab.getInstance().getSessionManager().getUser().authToken);

        HashMap<String, String> params = new HashMap<>();
        params.put("artistId", sCurCompId.isEmpty() ? String.valueOf(user.id) : sCurCompId);
        params.put("date", "2019-01-16");
        params.put("latitude", String.valueOf(Mualab.currentLat));
        params.put("longitude", String.valueOf(Mualab.currentLng));
        params.put("type", "all");
        params.put("staffId", staffId);
        params.put("bookingType", bookingType);

        getDataManager().doGetArtistBookingHistory(header, params).getAsString(new StringRequestListener() {

            @Override
            public void onResponse(String response) {
                AppLogger.e("onResponse", response);
                setLoading(false);
                BookingHistory bookingHistory = getDataManager().getGson().fromJson(response, BookingHistory.class);

                if (bookingHistory.getStatus().equalsIgnoreCase("success")) {
                    pendingBookingHistory.clear();

                    for (BookingHistory.DataBean tempBean : bookingHistory.getData()) {
                        if (tempBean.getBookStatus().equals("0")) {
                            pendingBookingHistory.add(tempBean);
                        }
                    }
                    pendBookingsAdapter.notifyDataSetChanged();
                    updateUI();
                } else {
                    MyToast.getInstance(getActivity()).showSmallMessage(bookingHistory.getMessage());
                    pendingBookingHistory.clear();
                    pendBookingsAdapter.notifyDataSetChanged();
                    updateUI();
                }
            }

            @Override
            public void onError(ANError anError) {
                setLoading(false);
                updateUI();
                Helper helper = new Helper();
                helper.parseError(anError.getErrorBody());
            }
        });
    }

    private void updateUI() {
        tvNoRecord.setVisibility(pendingBookingHistory.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spBkType:
                bkTypeAdapter.selectedPos = position;
                String bkType = bkTypeList.get(spBkType.getSelectedItemPosition()).name;
                String bkStaffId = artistStaffList.isEmpty() ? "" : artistStaffList.get(spBkStaff.getSelectedItemPosition()).staffId;

                doGetPendingBookingHistory(bkStaffId, bkType);
                break;

            case R.id.spBkStaff:
                if (isOnItemSelectedRun) {
                    isOnItemSelectedRun = false;
                    return;
                }
                bkStaffAdapter.selectedPos = position;
                bkType = bkTypeList.get(spBkType.getSelectedItemPosition()).name;
                bkStaffId = artistStaffList.isEmpty() ? "" : artistStaffList.get(spBkStaff.getSelectedItemPosition()).staffId;

                doGetPendingBookingHistory(bkStaffId, bkType);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
