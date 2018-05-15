package com.mualab.org.biz.modules.booking.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.mualab.org.biz.R;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.booking.activity.BookingDetailActivity;
import com.mualab.org.biz.modules.booking.activity.StaffActivity;
import com.mualab.org.biz.modules.booking.adapter.PendingBookingAdapter;
import com.mualab.org.biz.modules.booking.adapter.TimeSlotAdapter;
import com.mualab.org.biz.modules.booking.adapter.TodayBookingAdapter;
import com.mualab.org.biz.modules.booking.listner.OnBookingListListener;
import com.mualab.org.biz.modules.booking.listner.PendingBookingListener;
import com.mualab.org.biz.modules.booking.listner.TimeSlotClickListener;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.dialogs.Progress;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.model.User;
import com.mualab.org.biz.model.booking.Bookings;
import com.mualab.org.biz.model.booking.BookingInfo;
import com.mualab.org.biz.model.booking.BookingTimeSlot;
import com.mualab.org.biz.model.booking.Staff;
import com.mualab.org.biz.model.booking.UserDetail;
import com.mualab.org.biz.session.Session;
import com.mualab.org.biz.task.HttpResponceListner;
import com.mualab.org.biz.task.HttpTask;
import com.mualab.org.biz.util.ConnectionDetector;
import com.mualab.org.biz.util.Constant;
import com.mualab.org.biz.util.Helper;
import com.mualab.org.biz.util.LocationDetector;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import views.calender.data.CalendarAdapter;
import views.calender.data.Day;
import views.calender.widget.widget.MyFlexibleCalendar;


public class BookingsFragment extends Fragment implements View.OnClickListener, TimeSlotClickListener, PendingBookingListener, OnBookingListListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private long mLastClickTime = 0;
    private String sMonth = "", sDay, selectedDate, lat = "22.7196", lng = "75.8577", staffId = "";
    private Context mContext;
    private LinearLayout tabToday, tabPending;
    private TextView tvPending, tvToday, tvBookingCount;
    private List<BookingTimeSlot> bookingTimeSlots;
    private List<Bookings> todayBookings, pendingBookings;
    private List<Staff> staffList;
    private TimeSlotAdapter timeSlotAdapter;
    private TodayBookingAdapter todayBookingAdapter;
    private PendingBookingAdapter pendingBookingAdapter;
    private TextView tvNoData, tvNoSlot, tv_msg, tvStaffName;
    private RecyclerView rycTimeSlot, rycToday, rycPending;
    private RelativeLayout rlStaffName;
    private int dayId, count = 0;
    private boolean isToday = true, isCurrentDate = true;
    private View rootView;
    private SimpleDateFormat dateSdf, timeSdf;
    private ProgressBar progress_bar;

    public BookingsFragment() {
        // Required empty public constructor
    }


    public static BookingsFragment newInstance(String param1) {
        BookingsFragment fragment = new BookingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_booking, container, false);
        initView(rootView);
        // Inflate the layout for this fragment
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    private void initView(View rootView) {
        dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        timeSdf = new SimpleDateFormat("hh:mm a");
        // init calendar
        MyFlexibleCalendar viewCalendar = rootView.findViewById(R.id.calendar);
        Calendar cal = Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(mContext, cal);
        viewCalendar.setAdapter(adapter);
        progress_bar = rootView.findViewById(R.id.progress_bar);
        staffList = new ArrayList<>();
        bookingTimeSlots = new ArrayList<>();
        todayBookings = new ArrayList<>();
        pendingBookings = new ArrayList<>();

        timeSlotAdapter = new TimeSlotAdapter(mContext, bookingTimeSlots);
        todayBookingAdapter = new TodayBookingAdapter(mContext, todayBookings, staffList);
        pendingBookingAdapter = new PendingBookingAdapter(mContext, pendingBookings, staffList);

        setCalenderClickListner(viewCalendar);

        selectedDate = getCurrentDate();
        dayId = cal.get(GregorianCalendar.DAY_OF_WEEK) - 2;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViewId(view);
    }

    private void setViewId(View rootView) {
        if (mContext instanceof MainActivity) {
            ((MainActivity) mContext).setTitle(getString(R.string.title_bookings));
            ((MainActivity) mContext).setBackButtonVisibility(8);
        }
        tabToday = rootView.findViewById(R.id.tabToday);
        tabPending = rootView.findViewById(R.id.tabPending);
        tvPending = rootView.findViewById(R.id.tvPending);
        tvToday = rootView.findViewById(R.id.tvToday);
        tvNoData = rootView.findViewById(R.id.tvNoData);
        tvNoSlot = rootView.findViewById(R.id.tvNoSlot);
        tv_msg = rootView.findViewById(R.id.tv_msg);
        tvStaffName = rootView.findViewById(R.id.tvStaffName);
        tvBookingCount = rootView.findViewById(R.id.tvBookingCount);
        rlStaffName = rootView.findViewById(R.id.rlStaffName);
        AppCompatButton btnStaff = rootView.findViewById(R.id.btnStaff);
        AppCompatButton btnToday = rootView.findViewById(R.id.btnToday);

        rycTimeSlot = rootView.findViewById(R.id.rycTimeSlot);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        layoutManager.scrollToPositionWithOffset(0, 0);
        rycTimeSlot.setLayoutManager(layoutManager);
        rycTimeSlot.setAdapter(timeSlotAdapter);

        rycToday = rootView.findViewById(R.id.rycToday);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rycToday.setLayoutManager(layoutManager2);
        rycToday.setNestedScrollingEnabled(false);
        rycToday.setAdapter(todayBookingAdapter);
        todayBookingAdapter.setBookingClickListner(BookingsFragment.this);

        rycPending = rootView.findViewById(R.id.rycPending);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rycPending.setLayoutManager(layoutManager3);
        rycToday.setNestedScrollingEnabled(false);
        rycPending.setAdapter(pendingBookingAdapter);
        pendingBookingAdapter.setCustomListener(BookingsFragment.this);
        pendingBookingAdapter.setBookingClickListner(BookingsFragment.this);

        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();
        if (user.businessType.equals("independent"))
            btnStaff.setVisibility(View.GONE);
        else
            btnStaff.setVisibility(View.VISIBLE);

        getDeviceLocation();
        //apiForGetFreeSlots();

        tabToday.setOnClickListener(this);
        tabPending.setOnClickListener(this);
        btnToday.setOnClickListener(this);
        btnStaff.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch (view.getId()) {
            case R.id.btnToday:
                MyFlexibleCalendar viewCalendar = rootView.findViewById(R.id.calendar);
                viewCalendar.isFirstimeLoad = true;
                Calendar cal = Calendar.getInstance();
                CalendarAdapter adapter = new CalendarAdapter(mContext, cal);
                viewCalendar.setAdapter(adapter);
                viewCalendar.expand(500);
                setCalenderClickListner(viewCalendar);
                selectedDate = getCurrentDate();
                dayId = cal.get(GregorianCalendar.DAY_OF_WEEK) - 2;
                isCurrentDate = true;
                apiForGetFreeSlots();

                break;

            case R.id.btnStaff:
                Intent intent = new Intent(mContext, StaffActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("ARRAYLIST", (Serializable) staffList);
                intent.putExtra("BUNDLE", args);
                startActivityForResult(intent, 5);
                break;

            case R.id.tabToday:
                isToday = true;
                tabToday.setBackgroundResource(R.drawable.bg_tab_selected);
                tabPending.setBackgroundResource(R.drawable.bg_tab_unselected);
                tvPending.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvToday.setTextColor(getResources().getColor(R.color.white));

                apiForGetFreeSlots();
                break;

            case R.id.tabPending:
                isToday = false;
                tabPending.setBackgroundResource(R.drawable.bg_second_tab_selected);
                tabToday.setBackgroundResource(R.drawable.bg_second_tab_unselected);
                tvToday.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvPending.setTextColor(getResources().getColor(R.color.white));

                apiForGetFreeSlots();
                break;
        }
    }

    private void setCalenderClickListner(final MyFlexibleCalendar viewCalendar) {
        viewCalendar.setCalendarListener(new MyFlexibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect() {
                Day day = viewCalendar.getSelectedDay();
                viewCalendar.isFirstimeLoad = false;
                Log.i(getClass().getName(), "Selected Day: "
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

                if (!selectedDate.equals(getCurrentDate())) {
                    isCurrentDate = false;
                } else {
                    isCurrentDate = true;
                }

                if (viewCalendar.isSelectedDay(day)) {
                    Calendar todayCal = Calendar.getInstance();
                    int cYear = todayCal.get(Calendar.YEAR);
                    int cMonth = todayCal.get(Calendar.MONTH) + 1;
                    int cDay = todayCal.get(Calendar.DAY_OF_MONTH);

                    int year = day.getYear();
                    int dayOfMonth = day.getDay();

                    if (year >= cYear && month >= cMonth) {
                        if (year == cYear && month == cMonth && dayOfMonth < cDay) {
                            Log.i("", "can't select previous date");
                        } else {
                            apiForGetFreeSlots();
                        }
                    }
                }
            }

            @Override
            public void onItemClick(View v) {
                viewCalendar.isFirstimeLoad = false;
                Day day = viewCalendar.getSelectedDay();
                Log.i(getClass().getName(), "The Day of Clicked View: "
                        + day.getYear() + "/" + (day.getMonth() + 1) + "/" + day.getDay());
            }

            @Override
            public void onDataUpdate() {
                Log.i(getClass().getName(), "Data Updated");
            }

            @Override
            public void onMonthChange() {
                Log.i(getClass().getName(), "Month Changed"
                        + ". Current Year: " + viewCalendar.getYear()
                        + ", Current Month: " + (viewCalendar.getMonth() + 1));
            }

            @Override
            public void onWeekChange(int position) {
                Log.i(getClass().getName(), "Week Changed"
                        + ". Current Year: " + viewCalendar.getYear()
                        + ", Current Month: " + (viewCalendar.getMonth() + 1)
                        + ", Current Week position of Month: " + position);
            }
        });
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm a");
        System.out.println("currentTime" + date.format(currentLocalTime));
        return date.format(currentLocalTime);
    }

    private String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (month < 10) {
            sMonth = "0" + month;
        } else {
            sMonth = String.valueOf(month);
        }

        if (day < 10) {
            sDay = "0" + day;
        } else {
            sDay = String.valueOf(day);
        }
        return year + "-" + sMonth + "-" + sDay;
    }

    private void getDeviceLocation() {

        if (Build.VERSION.SDK_INT >= 23) {

            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constant.MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                LocationDetector locationDetector = new LocationDetector();
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
                if (locationDetector.isLocationEnabled(getActivity()) &&
                        locationDetector.checkLocationPermission(getActivity())) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {

                                Mualab.currentLat = location.getLatitude();
                                Mualab.currentLng = location.getLongitude();

                                if (lng.equals("") && lat.equals("")) {
                                    lat = String.valueOf(location.getLatitude());
                                    lng = String.valueOf(location.getLongitude());
                                }
                                apiForGetFreeSlots();
                            }
                        }
                    });

                } else {
                    //   progress_bar.setVisibility(View.GONE);
                    tv_msg.setText(R.string.gps_permission_alert);
                    locationDetector.showLocationSettingDailod(getActivity());
                }
            }
        } else {
            apiForGetFreeSlots();
        }

    }

    private void apiForGetFreeSlots() {
        progress_bar.setVisibility(View.VISIBLE);
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForGetFreeSlots();
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("day", String.valueOf(dayId));
        params.put("latitude", lat);
        params.put("longitude", lng);
        params.put("date", selectedDate);
        params.put("staffId", staffId);

        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "artistFreeSlotNew", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("massage");
                    if (status.equalsIgnoreCase("success")) {

                        staffList.clear();
                        count = 0;
                        todayBookings.clear();
                        pendingBookings.clear();
                        bookingTimeSlots.clear();
                        rycTimeSlot.setVisibility(View.VISIBLE);
                        rycToday.setVisibility(View.VISIBLE);
                        tvNoData.setVisibility(View.GONE);
                        tvNoSlot.setVisibility(View.GONE);
                        bookingTimeSlots.clear();

                        parseBookingResponse(response);


                    } else {
                        progress_bar.setVisibility(View.GONE);
                        rycTimeSlot.setVisibility(View.GONE);
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                    //  showToast(message);
                } catch (Exception e) {
                    progress_bar.setVisibility(View.GONE);
                    Progress.hide(mContext);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                progress_bar.setVisibility(View.GONE);
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showDasuAlert(helper.error_Messages(error));
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

        task.execute(this.getClass().getName());
    }

    private void parseBookingResponse(String response) {
        try {
            JSONObject js = new JSONObject(response);

            JSONArray jsonArray = js.getJSONArray("timeSlots");
            if (jsonArray != null && jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    BookingTimeSlot item = new BookingTimeSlot();
                    item.time = jsonArray.getString(i);
                    item.isSelected = "0";
                    bookingTimeSlots.add(item);
                }
            } else {
                rycTimeSlot.setVisibility(View.GONE);
                tvNoSlot.setVisibility(View.VISIBLE);
            }

            //parsing for getting staff
            JSONArray arrStaffDetail = js.getJSONArray("staffDetail");
            if (arrStaffDetail != null && arrStaffDetail.length() != 0) {
                for (int l = 0; l < arrStaffDetail.length(); l++) {
                    JSONObject object = arrStaffDetail.getJSONObject(l);
                    Gson gson2 = new Gson();
                    Staff staff = gson2.fromJson(String.valueOf(object), Staff.class);
                    staff.isSelected = false;
                    staffList.add(staff);
                }
            }

            JSONArray todayArray = js.getJSONArray("today");
            parseTodaysBooking(todayArray);

            JSONArray pendingArray = js.getJSONArray("pending");
            parsePendingBooking(pendingArray);

            if (isCurrentDate) {
                tvToday.setText(getString(R.string.today));
            } else {
                tvToday.setText(getString(R.string.text_confirmed));
            }

            if (isToday) {
                if (todayBookings.size() != 0) {
                    rycToday.setVisibility(View.VISIBLE);
                    rycPending.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rycToday.setVisibility(View.GONE);
                    rycPending.setVisibility(View.GONE);
                }
            } else {
                if (pendingBookings.size() != 0) {
                    rycToday.setVisibility(View.GONE);
                    rycPending.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rycToday.setVisibility(View.GONE);
                    rycPending.setVisibility(View.GONE);
                }
            }

            count = pendingBookings.size();

            if (count == 0) {
                tvBookingCount.setVisibility(View.GONE);
            } else {
                tvBookingCount.setVisibility(View.VISIBLE);
            }

            String sCount = String.valueOf(count);
            tvBookingCount.setText(sCount);
            timeSlotAdapter.notifyDataSetChanged();
            todayBookingAdapter.notifyDataSetChanged();
            pendingBookingAdapter.notifyDataSetChanged();

            progress_bar.setVisibility(View.GONE);
            //  showToast(message);
        } catch (Exception e) {
            progress_bar.setVisibility(View.GONE);
            Progress.hide(mContext);
            e.printStackTrace();
        }
    }

    //parsing for today's booking
    private void parseTodaysBooking(JSONArray todayArray) {
        if (todayArray != null && todayArray.length() != 0) {
            for (int j = 0; j < todayArray.length(); j++) {
                String serviceName = "";
                JSONObject object = null;
                try {
                    object = todayArray.getJSONObject(j);
                    Bookings item = new Bookings();
                    item._id = object.getString("_id");
                    item.bookingDate = object.getString("bookingDate");
                    item.bookingTime = object.getString("bookingTime");
                    item.bookStatus = object.getString("bookStatus");
                    item.paymentType = object.getString("paymentType");
                    item.totalPrice = object.getString("totalPrice");

                    Date currentDate = dateSdf.parse(getCurrentDate());
                    Date date = dateSdf.parse(item.bookingDate);

                    if (date.after(currentDate) || date.equals(currentDate)) {
                        Date currentime = timeSdf.parse(getCurrentTime());
                        Date time = timeSdf.parse(item.bookingTime);
                      /*  if (date.equals(currentDate) && time.before(currentime)) {
                            System.out.println("Pastbooking");
                        } else {*/
                        //parsing for user detail
                        JSONArray arrUserDetail = object.getJSONArray("userDetail");
                        if (arrUserDetail != null && arrUserDetail.length() != 0) {
                            for (int k = 0; k < arrUserDetail.length(); k++) {
                                Gson gson = new Gson();
                                JSONObject userObj = arrUserDetail.getJSONObject(k);
                                item.userDetail = gson.fromJson(String.valueOf(userObj), UserDetail.class);
                            }
                        }
                        //parsing for getting booking Info
                        JSONArray arrBookingInfo = object.getJSONArray("bookingInfo");
                        if (arrBookingInfo != null && arrBookingInfo.length() != 0) {
                            for (int l = 0; l < arrBookingInfo.length(); l++) {
                                JSONObject bInfoObj = arrBookingInfo.getJSONObject(l);
                                Gson gson2 = new Gson();
                                BookingInfo bookingInfo = gson2.fromJson(String.valueOf(bInfoObj), BookingInfo.class);

                                if (serviceName.equals("")) {
                                    serviceName = bookingInfo.artistServiceName;
                                }
                                item.todayBookingInfos.add(bookingInfo);
                            }
                            item.artistServiceName = serviceName;
                        }
                        if (arrBookingInfo.length()!=0)
                            todayBookings.add(item);
                        //   }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } /*else {
            rycToday.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }*/
    }

    //parsing for pending booking
    private void parsePendingBooking(JSONArray pendingArray) {

        if (pendingArray != null && pendingArray.length() != 0) {
            for (int j = 0; j < pendingArray.length(); j++) {
                String serviceName = "";
                JSONObject object = null;
                try {
                    object = pendingArray.getJSONObject(j);
                    Bookings item = new Bookings();
                    item._id = object.getString("_id");
                    item.bookingDate = object.getString("bookingDate");
                    item.bookingTime = object.getString("bookingTime");
                    item.bookStatus = object.getString("bookStatus");
                    item.paymentType = object.getString("paymentType");
                    item.totalPrice = object.getString("totalPrice");

                    Date currentDate = dateSdf.parse(getCurrentDate());
                    Date date = dateSdf.parse(item.bookingDate);

                    if (date.after(currentDate) || date.equals(currentDate)) {
                        Date currentime = timeSdf.parse(getCurrentTime());
                        Date time = timeSdf.parse(item.bookingTime);
                        if (date.equals(currentDate) && time.before(currentime)) {
                            System.out.println("pastbooking");
                        } else {
                            JSONArray arrUserDetail = object.getJSONArray("userDetail");
                            if (arrUserDetail != null && arrUserDetail.length() != 0) {
                                for (int k = 0; k < arrUserDetail.length(); k++) {
                                    Gson gson = new Gson();
                                    JSONObject userObj = arrUserDetail.getJSONObject(k);
                                    item.userDetail = gson.fromJson(String.valueOf(userObj), UserDetail.class);
                                }
                            }
                            //parsing for getting booking Info
                            JSONArray arrBookingInfo = object.getJSONArray("bookingInfo");
                            if (arrBookingInfo != null && arrBookingInfo.length() != 0) {
                                for (int l = 0; l < arrBookingInfo.length(); l++) {
                                    JSONObject bInfoObj = arrBookingInfo.getJSONObject(l);
                                    Gson gson2 = new Gson();
                                    BookingInfo bookingInfo = gson2.fromJson(String.valueOf(bInfoObj), BookingInfo.class);

                                    if (serviceName.equals("")) {
                                        serviceName = bookingInfo.artistServiceName;
                                    }
                                    item.pendingBookingInfos.add(bookingInfo);
                                }
                                item.artistServiceName = serviceName;
                            }
                            pendingBookings.add(item);
                        }
                        //parsing for user detail
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }/*else{
            rycPending.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }*/
    }

    private void apiForBookingAction(final String type, final Bookings bookings, final String serviceId, final String subServiceId, final String artistServiceId) {
        Session session = Mualab.getInstance().getSessionManager();
        User user = session.getUser();

        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(mContext, new NoConnectionDialog.Listner() {
                @Override
                public void onNetworkChange(Dialog dialog, boolean isConnected) {
                    if (isConnected) {
                        dialog.dismiss();
                        apiForBookingAction(type, bookings, serviceId, subServiceId, artistServiceId);
                    }
                }
            }).show();
        }

        Map<String, String> params = new HashMap<>();
        params.put("artistId", String.valueOf(user.id));
        params.put("userId", bookings.userDetail._id);
        params.put("bookingId", bookings._id);
        params.put("serviceId", serviceId);
        params.put("subserviceId", subServiceId);
        params.put("artistServiceId", artistServiceId);
        params.put("type", type);


        HttpTask task = new HttpTask(new HttpTask.Builder(mContext, "bookingAction", new HttpResponceListner.Listener() {
            @Override
            public void onResponse(String response, String apiName) {
                try {
                    JSONObject js = new JSONObject(response);
                    String status = js.getString("status");
                    String message = js.getString("message");

                    if (status.equalsIgnoreCase("success")) {
                        if (bookings.bookStatus.equals("0")) {
                            pendingBookings.remove(bookings);
                            pendingBookingAdapter.notifyDataSetChanged();

                            if (pendingBookings.size() == 0) {
                                count = 0;
                                tvBookingCount.setVisibility(View.GONE);
                                rycPending.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
                            } else {
                                count--;
                            }
                            String sCount = String.valueOf(count);
                            tvBookingCount.setText(sCount);
                        }
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    } else {
                        MyToast.getInstance(mContext).showDasuAlert(message);
                    }
                } catch (Exception e) {
                    Progress.hide(mContext);
                    e.printStackTrace();
                }
            }

            @Override
            public void ErrorListener(VolleyError error) {
                try {
                    Helper helper = new Helper();
                    if (helper.error_Messages(error).contains("Session")) {
                        Mualab.getInstance().getSessionManager().logout();
                        //      MyToast.getInstance(BookingActivity.this).showSmallCustomToast(helper.error_Messages(error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        })
                .setAuthToken(user.authToken)
                .setProgress(true)
                .setBody(params, HttpTask.ContentType.APPLICATION_JSON));
        //.setBody(params, "application/x-www-form-urlencoded"));

        task.execute(this.getClass().getName());
    }

    @Override
    public void onButtonClick(int position, String buttonText, int selectedCount) {
        BookingTimeSlot item = bookingTimeSlots.get(position);
        for (int i = 0; i < bookingTimeSlots.size(); i++) {
            BookingTimeSlot timeSlot = bookingTimeSlots.get(i);
            timeSlot.isSelected = "0";
        }
        timeSlotAdapter.notifyDataSetChanged();
        // if (item.isSelected.equals("0"))
        item.isSelected = "1";
        //  else
        //     item.isSelected = "0";
        timeSlotAdapter.notifyItemChanged(position);
    }

    @Override
    public void onActionClick(int position, Bookings bookings, String text) {
        String serviceId = "", subServiceId = "", artistServiceId = "";
        if (bookings.pendingBookingInfos.size() != 0) {
            for (int i = 0; i < bookings.pendingBookingInfos.size(); i++) {
                BookingInfo bookingInfo = bookings.pendingBookingInfos.get(i);
                if (serviceId.equals("")) {
                    serviceId = bookingInfo.serviceId;
                } else {
                    if (!serviceId.contains(bookingInfo.serviceId))
                        serviceId = serviceId + "," + bookingInfo.serviceId;
                }
                if (subServiceId.equals("")) {
                    subServiceId = bookingInfo.subServiceId;
                } else {
                    if (!subServiceId.contains(bookingInfo.subServiceId))
                        subServiceId = subServiceId + "," + bookingInfo.subServiceId;
                }

                if (artistServiceId.equals("")) {
                    artistServiceId = bookingInfo.artistServiceId;
                } else {
                    if (!artistServiceId.contains(bookingInfo.artistServiceId))
                        artistServiceId = artistServiceId + "," + bookingInfo.artistServiceId;
                }

            }
            apiForBookingAction(text, bookings, serviceId, subServiceId, artistServiceId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getDeviceLocation();
                    }

                } else {
                    //Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_LONG).show();
                    apiForGetFreeSlots();
                }
            }

        }
    }

    public void refreshData(boolean isRefresh) {
        if (isRefresh)
            apiForGetFreeSlots();
    }

    @Override
    public void onItemClick(int position, List<Staff> staffList, String id) {
        Intent intent = new Intent(mContext, BookingDetailActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", (Serializable) staffList);
        intent.putExtra("BUNDLE", args);
        intent.putExtra("bookingId", id);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  Handle modules result here
        if (requestCode == 2 && resultCode != 0) {
            if (data != null) {
                tabToday.setBackgroundResource(R.drawable.bg_tab_selected);
                tabPending.setBackgroundResource(R.drawable.bg_tab_unselected);
                tvPending.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvToday.setTextColor(getResources().getColor(R.color.white));
                isToday = true;

                apiForGetFreeSlots();
            }
        } else if (requestCode == 5) {
            if (data != null) {
                staffId = data.getStringExtra("staffId");
                String staffName = data.getStringExtra("staffName");
                rlStaffName.setVisibility(View.VISIBLE);

                if (!staffId.equals(""))
                    tvStaffName.setText(staffName);
                else {
                    tvStaffName.setText("All Staff");
                }

                apiForGetFreeSlots();
            }
        }

    }

    private void parsePreviousData(String response) {
        try {
            JSONObject js = new JSONObject(response);
            JSONArray jsonArray = js.getJSONArray("timeSlots");
            if (jsonArray != null && jsonArray.length() != 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    BookingTimeSlot item = new BookingTimeSlot();
                    item.time = jsonArray.getString(i);
                    item.isSelected = "0";
                    bookingTimeSlots.add(item);
                }
            } else {
                rycTimeSlot.setVisibility(View.GONE);
                tvNoSlot.setVisibility(View.VISIBLE);
            }

            //parsing for getting staff
            JSONArray arrStaffDetail = js.getJSONArray("staffDetail");
            if (arrStaffDetail != null && arrStaffDetail.length() != 0) {
                for (int l = 0; l < arrStaffDetail.length(); l++) {
                    JSONObject object = arrStaffDetail.getJSONObject(l);
                    Gson gson2 = new Gson();
                    Staff staff = gson2.fromJson(String.valueOf(object), Staff.class);

                    /*Staff staff = new Staff();
                    staff.staffId = object.getString("staffId");
                    staff.staffName = object.getString("staffName");
                    staff.staffImage = object.getString("staffImage");
                    staff.isSelected = false;
                    staff.job = object.getString("job");*/
                    staff.isSelected = false;
                    staffList.add(staff);
                }
            }

            JSONArray array = js.getJSONArray("booking");
            if (array != null && array.length() != 0) {
                for (int j = 0; j < array.length(); j++) {
                    String serviceName = "";
                    JSONObject object = array.getJSONObject(j);
                    Bookings item = new Bookings();
                    item._id = object.getString("_id");
                    item.bookingDate = object.getString("bookingDate");
                    item.bookingTime = object.getString("bookingTime");
                    item.bookStatus = object.getString("bookStatus");
                    item.paymentType = object.getString("paymentType");
                    item.totalPrice = object.getString("totalPrice");

                    //parsing for user detail
                    JSONArray arrUserDetail = object.getJSONArray("userDetail");
                    if (arrUserDetail != null && arrUserDetail.length() != 0) {
                        for (int k = 0; k < arrUserDetail.length(); k++) {
                            Gson gson = new Gson();
                            JSONObject userObj = arrUserDetail.getJSONObject(k);
                            item.userDetail = gson.fromJson(String.valueOf(userObj), UserDetail.class);
                        }
                    }
                    //parsing for getting booking Info
                    JSONArray arrBookingInfo = object.getJSONArray("bookingInfo");
                    if (arrBookingInfo != null && arrBookingInfo.length() != 0) {
                        for (int l = 0; l < arrBookingInfo.length(); l++) {
                            JSONObject bInfoObj = arrBookingInfo.getJSONObject(l);
                            Gson gson2 = new Gson();
                            BookingInfo bookingInfo = gson2.fromJson(String.valueOf(bInfoObj), BookingInfo.class);

                            if (serviceName.equals("")) {
                                serviceName = bookingInfo.artistServiceName;
                            }//else
                            //serviceName = serviceName + ","+bookingInfo.artistServiceName;

                            if (item.bookStatus.equals("0")) {
                                item.pendingBookingInfos.add(bookingInfo);
                            } else {
                                item.todayBookingInfos.add(bookingInfo);
                            }
                        }
                        item.artistServiceName = serviceName;
                    }
                    if (item.bookStatus.equals("0")) {
                        count++;
                        pendingBookings.add(item);
                    } else {
                        todayBookings.add(item);
                    }
                }
            } else {
                rycToday.setVisibility(View.GONE);
                rycPending.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }

            if (isToday) {
                if (todayBookings.size() != 0) {
                    rycToday.setVisibility(View.VISIBLE);
                    rycPending.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rycToday.setVisibility(View.GONE);
                    rycPending.setVisibility(View.GONE);
                }
            } else {
                if (pendingBookings.size() != 0) {
                    rycToday.setVisibility(View.GONE);
                    rycPending.setVisibility(View.VISIBLE);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    rycToday.setVisibility(View.GONE);
                    rycPending.setVisibility(View.GONE);
                }
            }

            if (count > 0) {
                tvBookingCount.setVisibility(View.VISIBLE);
            } else {
                tvBookingCount.setVisibility(View.GONE);
            }

            String sCount = String.valueOf(count);
            tvBookingCount.setText(sCount);
            timeSlotAdapter.notifyDataSetChanged();
            todayBookingAdapter.notifyDataSetChanged();
            pendingBookingAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Progress.hide(mContext);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Mualab.getInstance().cancelAllPendingRequests();
        super.onDestroyView();
    }
}
