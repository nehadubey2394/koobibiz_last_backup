package com.mualab.org.biz.modules.new_booking.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.dialogs.NoConnectionDialog;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.helper.MyToast;
import com.mualab.org.biz.modules.base.BaseFragment;
import com.mualab.org.biz.modules.new_booking.activity.BookingDetailActivity;
import com.mualab.org.biz.modules.new_booking.adapter.BookingsAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.CallTypeArrayAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.MyBookingArrayAdapter;
import com.mualab.org.biz.modules.new_booking.model.BookingFilterModel;
import com.mualab.org.biz.modules.new_booking.model.BookingHistory;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.util.AppLogger;
import com.mualab.org.biz.util.CalanderUtils;
import com.mualab.org.biz.util.ConnectionDetector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import views.calender.data.CalendarAdapter;
import views.calender.data.Day;
import views.calender.widget.widget.MyFlexibleCalendar;

public class BookingsFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private String sMonth = "", sDay, selectedDate;

    private TextView tv_msg, tvNoRecord;
    private int dayId;
    private SimpleDateFormat dateSdf, timeSdf;
    private ProgressBar progress_bar;

    private PreRegistrationSession pSession;

    private MyFlexibleCalendar viewCalendar;
    private Spinner spBkDate, spBkType;
    private MyBookingArrayAdapter bkDateAdapter;
    private CallTypeArrayAdapter bkTypeAdapter;

    private List<BookingFilterModel> bkTempList, bkPrevDate, bkTodayDate, bkAfterdate, bkTypeList;
    private List<BookingHistory.DataBean> bookingHistoryList;
    private BookingsAdapter bookingsAdapter;
    private Boolean isTodayClick = false;

    public static BookingsFragment newInstance() {

        Bundle args = new Bundle();

        BookingsFragment fragment = new BookingsFragment();
        fragment.setArguments(args);
        return fragment;
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
        dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        timeSdf = new SimpleDateFormat("hh:mm a", Locale.US);

        tvNoRecord = view.findViewById(R.id.tvNoRecord);
        progress_bar = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.btnAdd).setOnClickListener(this);
        Button btnToday = view.findViewById(R.id.btnToday);
        btnToday.setOnClickListener(this);
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
        bookingsAdapter = new BookingsAdapter(bookingHistoryList, pos -> startActivity(new Intent(getBaseActivity(), BookingDetailActivity.class)));
        rvBookings.setAdapter(bookingsAdapter);
    }

    private void initSpinner(View view) {
        setBookingFilterData();
        spBkDate = view.findViewById(R.id.spBkDate);
        spBkType = view.findViewById(R.id.spBkType);

        pSession = Mualab.getInstance().getBusinessProfileSession();

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

    private void doGetArtistBookingHistory(String date, String type, String staffId, String bookingType) {
        if (!ConnectionDetector.isConnected()) {
            new NoConnectionDialog(getBaseActivity(), (dialog, isConnected) -> {
                if (isConnected) {
                    dialog.dismiss();
                    doGetArtistBookingHistory(date, type, staffId, bookingType);
                }
            }).show();
        }

        setLoading(true);
        HashMap<String, String> header = new HashMap<>();
        header.put("authToken", Mualab.getInstance().getSessionManager().getUser().authToken);

        HashMap<String, String> params = new HashMap<>();
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
                setLoading(false);
                BookingHistory bookingHistory = getDataManager().getGson().fromJson(response, BookingHistory.class);
                bookingHistoryList.clear();
                bookingHistoryList.addAll(bookingHistory.getData());
                bookingsAdapter.notifyDataSetChanged();

                updateUI();
            }

            @Override
            public void onError(ANError anError) {
                setLoading(false);
                updateUI();
                AppLogger.e("onError", anError.getErrorBody() + " \n" + anError.getErrorDetail() + " \n" + anError.getResponse());
            }
        });
    }

    private void updateUI() {
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

                doGetArtistBookingHistory(selectedDate, bkTempList.get(spBkDate.getSelectedItemPosition()).name, "", String.valueOf(pSession.getServiceType()));
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

                if (isTodayClick)
                    doGetArtistBookingHistory(selectedDate, bkTempList.get(spBkDate.getSelectedItemPosition()).name, "", String.valueOf(pSession.getServiceType()));
                else isTodayClick = true;
                break;

            case R.id.btnAdd:
                MyToast.getInstance(getBaseActivity()).showSmallMessage(getString(R.string.under_development));
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

                doGetArtistBookingHistory(selectedDate, bkDateType, "", bkType);
                break;

            case R.id.spBkType:
                bkTypeAdapter.selectedPos = position;
                bkDateType = bkTempList.get(spBkDate.getSelectedItemPosition()).name;
                bkType = bkTypeList == null ? "" : bkTypeList.get(spBkType.getSelectedItemPosition()).name;

                doGetArtistBookingHistory(selectedDate, bkDateType, "", bkType);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
