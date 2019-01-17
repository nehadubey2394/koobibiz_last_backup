package com.mualab.org.biz.modules.new_booking.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.model.company_management.CompanyDetail;
import com.mualab.org.biz.modules.base.BaseFragment;
import com.mualab.org.biz.modules.new_booking.activity.BookingDetailActivity;
import com.mualab.org.biz.modules.new_booking.adapter.BookingsAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.CallTypeArrayAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.MyBookingArrayAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.StaffFilterAdapter;
import com.mualab.org.biz.modules.new_booking.dialog.CompanyDeleteAlertDialog;
import com.mualab.org.biz.modules.new_booking.model.BookingFilterModel;
import com.mualab.org.biz.modules.new_booking.model.BookingHistory;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.AppLogger;
import com.mualab.org.biz.util.CalanderUtils;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import views.calender.data.CalendarAdapter;
import views.calender.data.Day;
import views.calender.widget.widget.MyFlexibleCalendar;

public class BookingsFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final static String TAG = BookingsFragment.class.getName();
    private static String lastStaffId = "";
    private String sMonth = "", sDay, selectedDate;
    private TextView tv_msg, tvNoRecord, tvStaffNoRecord;
    private ImageView imgRight;
    private int dayId;
    private SimpleDateFormat dateSdf, timeSdf;
    private ProgressBar progress_bar, popupProgress;
    private PreRegistrationSession pSession;
    private User user;
    private MyFlexibleCalendar viewCalendar;
    private RecyclerView rvStaff;
    private Spinner spBkDate, spBkType;
    private MyBookingArrayAdapter bkDateAdapter;
    private CallTypeArrayAdapter bkTypeAdapter;
    private List<Staff> artistStaffs;
    private List<BookingFilterModel> bkTempList, bkPrevDate, bkTodayDate, bkAfterdate, bkTypeList;
    private List<BookingHistory.DataBean> bookingHistoryList;
    private BookingsAdapter bookingsAdapter;
    private StaffFilterAdapter staffFilterAdapter;
    private Boolean isTodayClick = false;
    private String sCurCompId = "";
    private CompanyDeleteAlertDialog alertDialog;

    public static BookingsFragment newInstance() {

        Bundle args = new Bundle();

        BookingsFragment fragment = new BookingsFragment();
        lastStaffId = "";
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = Mualab.getInstance().getSessionManager().getUser();
        pSession = Mualab.getInstance().getBusinessProfileSession();
        sCurCompId = pSession.getCurrentCompanyDetail()._id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        imgRight = getBaseActivity().findViewById(R.id.imgRight);
        dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        timeSdf = new SimpleDateFormat("hh:mm a", Locale.US);

        tvNoRecord = view.findViewById(R.id.tvNoRecord);
        progress_bar = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.btnAdd).setOnClickListener(this);
        Button btnToday = view.findViewById(R.id.btnToday);
        btnToday.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        // init calendar
        viewCalendar = view.findViewById(R.id.calendar);
        btnToday.callOnClick();

        tv_msg = view.findViewById(R.id.tv_msg);

        initSpinner(view);
        initBookingRecycler(view);
    }

    private void initBookingRecycler(View view) {
        RecyclerView rvBookings = view.findViewById(R.id.rvBookings);

        bookingHistoryList = new ArrayList<>();
        bookingsAdapter = new BookingsAdapter(bookingHistoryList, pos -> {
            Intent intent = new Intent(getBaseActivity(), BookingDetailActivity.class);
            intent.putExtra(Constants.BOOKING_ID, String.valueOf(bookingHistoryList.get(pos).get_id()));
            startActivity(intent);
        });
        rvBookings.setAdapter(bookingsAdapter);
    }

    private void initSpinner(View view) {
        setBookingFilterData();
        spBkDate = view.findViewById(R.id.spBkDate);
        spBkType = view.findViewById(R.id.spBkType);

        bkDateAdapter = new MyBookingArrayAdapter(getBaseActivity(), bkTempList);
        spBkDate.setAdapter(bkDateAdapter);
        spBkDate.setSelection(1);  //default set Todays booking
        spBkDate.setOnItemSelectedListener(this);

        //1:  Incall , 2: Outcall , 3: Both
        String selectedCompType = pSession.getCurrentCompanyDetail().serviceTypeOfCompany;
        if (selectedCompType.equals("Both") || pSession.getServiceType() == 3 && selectedCompType.isEmpty()) {
            view.findViewById(R.id.rlBkType).setVisibility(View.VISIBLE);

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

            bkTypeAdapter = new CallTypeArrayAdapter(getBaseActivity(), bkTypeList);
            spBkType.setAdapter(bkTypeAdapter);
            spBkType.setOnItemSelectedListener(this);
        }
    }

    private void setBookingFilterData() {
        bkTempList = new ArrayList<>();
        bkPrevDate = new ArrayList<>();
        bkTodayDate = new ArrayList<>();
        bkAfterdate = new ArrayList<>();

        //"All Booking","Todays Booking","Cancelled Booking","Complete Booking”,"Confirm Booking”,”Pending Booking”

        BookingFilterModel filterModel = new BookingFilterModel();
        filterModel.displayName = "All Booking";
        filterModel.name = "All Booking";
        bkPrevDate.add(filterModel);
        bkTodayDate.add(filterModel);
        bkAfterdate.add(filterModel);

        filterModel = new BookingFilterModel();
        filterModel.displayName = "Todays Booking";
        filterModel.name = "Todays Booking";
        bkTodayDate.add(filterModel);

        filterModel = new BookingFilterModel();
        filterModel.displayName = "Cancelled Booking";
        filterModel.name = "Cancelled Booking";
        bkPrevDate.add(filterModel);
        bkTodayDate.add(filterModel);
        bkAfterdate.add(filterModel);

        filterModel = new BookingFilterModel();
        filterModel.displayName = "Complete Booking";
        filterModel.name = "Complete Booking";
        bkPrevDate.add(filterModel);
        bkTodayDate.add(filterModel);

        filterModel = new BookingFilterModel();
        filterModel.displayName = "Confirm Booking";
        filterModel.name = "Confirm Booking";
        bkAfterdate.add(filterModel);

        bkTempList.addAll(bkTodayDate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Mualab.currentLat == 0.0) {
            Mualab.currentLat = Double.parseDouble(pSession.getAddress().latitude);
            Mualab.currentLng = Double.parseDouble(pSession.getAddress().longitude);
        }

        //type -> "All Booking","Todays Booking","Cancelled Booking","Complete Booking”,
        // "Confirm Booking”,”Pending Booking”
        //doGetArtistBookingHistory(selectedDate, bkTempList.get(spBkDate.getSelectedItemPosition()).name, "", String.valueOf(pSession.getServiceType()));
    }

    private void updateUI(String businessType) {
        imgRight.setVisibility(businessType.equalsIgnoreCase("business") ? View.VISIBLE : View.GONE);
        tvNoRecord.setVisibility(bookingHistoryList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setCalenderClickListner(final MyFlexibleCalendar viewCalendar) {
        viewCalendar.setCalendarListener(new MyFlexibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = viewCalendar.getSelectedDay();
                viewCalendar.isFirstimeLoad = false;
                AppLogger.i(getClass().getName(), "Selected Day: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());

                Date date = new Date(day.getYear(), day.getMonth(), day.getDay() - 1);
                dayId = date.getDay() - 1;

                if (dayId == -1) {
                    dayId = 6;
                }

                int month = day.getMonth() + 1;

                if (month < 10) {
                    sMonth = "0" + month;
                } else {
                    sMonth = String.valueOf(month);
                }

                if (day.getDay() < 10) {
                    sDay = "0" + day.getDay();
                } else {
                    sDay = String.valueOf(day.getDay());
                }
                selectedDate = day.getYear() + "-" + sMonth + "-" + sDay;

                Date selectedDateTemp = CalanderUtils.getDateFormat(selectedDate, Constants.SERVER_TIMESTAMP_FORMAT);
                Date today = CalanderUtils.getDateFormat(CalanderUtils.formatDate(CalanderUtils.getCurrentDate(), Constants.SERVER_TIMESTAMP_FORMAT, Constants.SERVER_TIMESTAMP_FORMAT), Constants.SERVER_TIMESTAMP_FORMAT);

                assert selectedDateTemp != null;
                if (selectedDateTemp.before(today)) {
                    bkTempList.clear();
                    bkTempList.addAll(bkPrevDate);
                    bkDateAdapter.notifyDataSetChanged();
                    spBkDate.setSelection(0);
                } else if (selectedDateTemp.after(today)) {
                    bkTempList.clear();
                    bkTempList.addAll(bkAfterdate);
                    bkDateAdapter.notifyDataSetChanged();
                    spBkDate.setSelection(0);
                } else {
                    bkTempList.clear();
                    bkTempList.addAll(bkTodayDate);
                    bkDateAdapter.notifyDataSetChanged();
                    spBkDate.setSelection(1);
                }

                doGetArtistBookingHistory(selectedDate, bkTempList.get(spBkDate.getSelectedItemPosition()).name, lastStaffId, String.valueOf(pSession.getServiceType()));
            }

            @Override
            public void onItemClick(View v) {
                viewCalendar.isFirstimeLoad = false;
                Day day = viewCalendar.getSelectedDay();
                AppLogger.i(getClass().getName(), "The Day of Clicked View: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
            }

            @Override
            public void onDataUpdate() {
                AppLogger.i(getClass().getName(), "Data Updated");
            }

            @Override
            public void onMonthChange() {
                AppLogger.i(getClass().getName(), "Month Changed"
                        + ". Current Year: " + viewCalendar.getYear()
                        + ", Current Month: " + (viewCalendar.getMonth() + 1));
            }

            @Override
            public void onWeekChange(int position) {
                AppLogger.i(getClass().getName(), "Week Changed"
                        + ". Current Year: " + viewCalendar.getYear()
                        + ", Current Month: " + (viewCalendar.getMonth() + 1)
                        + ", Current Week position of Month: " + position);
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnToday:
                viewCalendar.isFirstimeLoad = true;
                Calendar cal = Calendar.getInstance();
                CalendarAdapter adapter = new CalendarAdapter(getBaseActivity(), cal);
                viewCalendar.setAdapter(adapter);
                //viewCalendar.expand(500);
                setCalenderClickListner(viewCalendar);
                selectedDate = CalanderUtils.formatDate(CalanderUtils.getCurrentDate(), Constants.SERVER_TIMESTAMP_FORMAT, Constants.SERVER_TIMESTAMP_FORMAT);
                dayId = cal.get(GregorianCalendar.DAY_OF_WEEK) - 2;

                if (isTodayClick) {
                    bkTempList.clear();
                    bkTempList.addAll(bkTodayDate);
                    bkDateAdapter.notifyDataSetChanged();
                    spBkDate.setSelection(1);
                } else isTodayClick = true;
                break;

            case R.id.btnAdd:
                MyToast.getInstance(getBaseActivity()).showSmallMessage(getString(R.string.under_development));
                break;

            case R.id.imgRight:
                //user type business then open popupFilter
                /*if (!user.businessType.equals("independent")) {

                } else {
                    startActivity(new Intent(this, PendingBookingActivity.class));
                }*/
                int[] point = new int[2];

                // Get the x, y location and store it in the location[] array
                // location[0] = x, location[1] = y.
                imgRight.getLocationOnScreen(point);

                //Initialize the Point with x, and y positions
                Display display = getBaseActivity().getWindowManager().getDefaultDisplay();
                Point p = new Point();
                display.getSize(p);
                p.x = point[0];
                p.y = point[1];

                popupFilter(p);
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spBkDate:
                bkDateAdapter.selectedPos = position;
                String bkDateType = bkTempList.get(spBkDate.getSelectedItemPosition()).name;
                String bkType = bkTypeList == null ? "" : bkTypeList.get(spBkType.getSelectedItemPosition()).name;

                doGetArtistBookingHistory(selectedDate, bkDateType, lastStaffId, bkType);
                break;

            case R.id.spBkType:
                bkTypeAdapter.selectedPos = position;
                bkDateType = bkTempList.get(spBkDate.getSelectedItemPosition()).name;
                bkType = bkTypeList == null ? "" : bkTypeList.get(spBkType.getSelectedItemPosition()).name;

                doGetArtistBookingHistory(selectedDate, bkDateType, lastStaffId, bkType);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void doGetArtistBookingHistory(String date, String type, String staffId, String bookingType) {

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(getBaseActivity(), (dialog, isConnected) -> {
                if (isConnected) {
                    dialog.dismiss();
                    doGetArtistBookingHistory(date, type, staffId, bookingType);
                }
            }).show();
        }

        setDashboardApiLoader(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("authToken", Mualab.getInstance().getSessionManager().getUser().authToken);

        HashMap<String, String> params = new HashMap<>();
        params.put("artistId", sCurCompId.isEmpty() ? String.valueOf(user.id) : sCurCompId);
        params.put("date", date);
        params.put("latitude", String.valueOf(Mualab.currentLat));
        params.put("longitude", String.valueOf(Mualab.currentLng));
        params.put("type", type);
        params.put("staffId", staffId);
        params.put("bookingType", bookingType.equals("3") ? "" : bookingType);

        getDataManager().doGetArtistBookingHistory(header, params).getAsString(new StringRequestListener() {

            @Override
            public void onResponse(String response) {
                AppLogger.e("onResponse", response);
                setDashboardApiLoader(false);
                BookingHistory bookingHistory = getDataManager().getGson().fromJson(response, BookingHistory.class);

                if (bookingHistory.getStatus().equalsIgnoreCase("success")) {
                    bookingHistoryList.clear();
                    bookingHistoryList.addAll(bookingHistory.getData());
                    bookingsAdapter.isAllBooking = type.equalsIgnoreCase("All Booking");
                    bookingsAdapter.notifyDataSetChanged();
                    updateUI(bookingHistory.getBusinessType());
                } else {
                    //this case arise when someone delete user selected company
                    if (bookingHistory.getMessage().equalsIgnoreCase("Company not exist")) {
                        if (alertDialog != null) alertDialog.dismiss();
                        alertDialog = new CompanyDeleteAlertDialog();
                        alertDialog.setOnClickListener(pSession.getCurrentCompanyDetail().businessName, () -> {
                            pSession.createCurrentCompanyDetail(new CompanyDetail());
                            getBaseActivity().replaceFragment(BookingsFragment.newInstance(), false);
                        });
                        alertDialog.show(getChildFragmentManager());
                        return;
                    }

                    MyToast.getInstance(getActivity()).showSmallMessage(bookingHistory.getMessage());
                    bookingHistoryList.clear();
                    bookingsAdapter.isAllBooking = type.equalsIgnoreCase("All Booking");
                    bookingsAdapter.notifyDataSetChanged();
                    updateUI(bookingHistory.getBusinessType());
                }
            }

            @Override
            public void onError(ANError anError) {
                setDashboardApiLoader(false);
                updateUI("");
                //AppLogger.e("onError", anError.getErrorBody() + " \n" + anError.getErrorDetail() + " \n" + anError.getResponse());
                Helper helper = new Helper();
                helper.parseError(anError.getErrorBody());
            }
        });
    }

    private void setDashboardApiLoader(Boolean isLoading) {
        progress_bar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void setPopupApiLoader(Boolean isLoading) {
        popupProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void apiForGetArtistStaff() {
        setPopupApiLoader(true);

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(getBaseActivity(), (dialog, isConnected) -> {
                if(isConnected){
                    dialog.dismiss();
                    apiForGetArtistStaff();
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", sCurCompId.isEmpty() ? String.valueOf(user.id) : sCurCompId);
        params.put("search", "");

        HttpTask task = new HttpTask(new HttpTask.Builder(getBaseActivity(), "artistStaff", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    setPopupApiLoader(false);
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        artistStaffs.clear();

                        JSONArray jsonArray = js.getJSONArray("staffList");
                        if (jsonArray!=null && jsonArray.length()!=0) {
                            for (int i=0; i<jsonArray.length(); i++){
                                // Staff item = new Staff();
                                Gson gson = new Gson();
                                JSONObject object = jsonArray.getJSONObject(i);
                                Staff item = gson.fromJson(String.valueOf(object), Staff.class);
                                //for preselected
                                if (!lastStaffId.isEmpty() && item.staffId.equals(lastStaffId)) {
                                    item.isSelected = true;
                                }
                                artistStaffs.add(item);
                            }
                            updateStaffFilterUI();
                        }else {
                            updateStaffFilterUI();
                        }

                    }else {
                        updateStaffFilterUI();
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    setPopupApiLoader(false);
                    e.printStackTrace();
                    updateStaffFilterUI();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                setPopupApiLoader(false);
                try{
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")){
                        Mualab.getInstance().getSessionManager().logout();
                        // MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


            }})
                .setAuthToken(user.authToken)
                .setProgress(false)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(TAG);
    }

    private void updateStaffFilterUI() {
        tvStaffNoRecord.setVisibility(artistStaffs.isEmpty() ? View.VISIBLE : View.GONE);
        staffFilterAdapter.notifyDataSetChanged();
    }

    //Todo create base popup class
    private void popupFilter(Point p) {

        try {
            LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View layout = inflater.inflate(R.layout.popup_menu_layout, null);
            layout.setAnimation(AnimationUtils.loadAnimation(getBaseActivity(), R.anim.popupanim));

            final PopupWindow popupWindow = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                    true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                popupWindow.setElevation(5);
            }

            String reqString = Build.MANUFACTURER
                    + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                    + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();


            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.setOutsideTouchable(true);
            popupWindow.setFocusable(true);

            int OFFSET_X;
            int OFFSET_Y;

            if (reqString.equals("motorola Moto G (4) 7.0 M")) {
                OFFSET_X = 480;
                OFFSET_Y = 70;
            } else {
                OFFSET_X = 480;
                OFFSET_Y = 48;
            }

            popupWindow.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
            rvStaff = layout.findViewById(R.id.rvStaff);
            tvStaffNoRecord = layout.findViewById(R.id.tvNoDataFound);
            popupProgress = layout.findViewById(R.id.popupProgress);
            Button btnAllStaff = layout.findViewById(R.id.btnAllStaff);

            btnAllStaff.setOnClickListener(v -> {
                popupWindow.dismiss();
                imgRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_filter));
                popupWindowOptionClicks("All Staff");
            });

            initPopupRecycler(popupWindow);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPopupRecycler(PopupWindow popupWindow) {
        artistStaffs = new ArrayList<>();
        staffFilterAdapter = new StaffFilterAdapter(artistStaffs, pos -> {
            final String data = artistStaffs.get(pos).staffId;
            popupWindow.dismiss();

            if (!ConnectionDetector.isConnected()) {
                new NoConnectionDialog(getBaseActivity(), (dialog, isConnected) -> {
                    if (isConnected) {
                        dialog.dismiss();
                        popupWindowOptionClicks(data);
                    }
                }).show();
            } else {
                imgRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_apply_filter));
                popupWindowOptionClicks(data);
            }
        });

        updateRecyclerHeight();
        rvStaff.setAdapter(staffFilterAdapter);

        //getStaff
        apiForGetArtistStaff();
    }

    private void updateRecyclerHeight() {
        if (artistStaffs.size() > 4) {
            ViewGroup.LayoutParams params = rvStaff.getLayoutParams();
            params.height = 480;
            rvStaff.setLayoutParams(params);
        }
    }

    private void popupWindowOptionClicks(String staffId) {

        switch (staffId) {
            case "All Staff":
                lastStaffId = "";
                String bkDateType = bkTempList.get(spBkDate.getSelectedItemPosition()).name;
                String bkType = bkTypeList == null ? "" : bkTypeList.get(spBkType.getSelectedItemPosition()).name;

                doGetArtistBookingHistory(selectedDate, bkDateType, lastStaffId, bkType);
                break;

            default:
                lastStaffId = staffId;
                bkDateType = bkTempList.get(spBkDate.getSelectedItemPosition()).name;
                bkType = bkTypeList == null ? "" : bkTypeList.get(spBkType.getSelectedItemPosition()).name;

                doGetArtistBookingHistory(selectedDate, bkDateType, staffId, bkType);
                break;

        }
    }

}
