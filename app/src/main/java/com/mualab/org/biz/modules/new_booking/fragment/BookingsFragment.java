package com.mualab.org.biz.modules.new_booking.fragment;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.mualab.org.biz.R;
import com.mualab.org.biz.application.Mualab;
import com.mualab.org.biz.helper.Constants;
import com.mualab.org.biz.model.booking.Bookings;
import com.mualab.org.biz.modules.MainActivity;
import com.mualab.org.biz.modules.base.BaseFragment;
import com.mualab.org.biz.modules.new_booking.adapter.BookingsAdapter;
import com.mualab.org.biz.modules.new_booking.adapter.MyArrayAdapter;
import com.mualab.org.biz.session.PreRegistrationSession;
import com.mualab.org.biz.util.AppLogger;
import com.mualab.org.biz.util.CalanderUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import views.calender.data.CalendarAdapter;
import views.calender.data.Day;
import views.calender.widget.widget.MyFlexibleCalendar;


public class BookingsFragment extends BaseFragment implements View.OnClickListener {

    private long mLastClickTime = 0;
    private String sMonth = "", sDay, selectedDate;

    private TextView tv_msg;
    private int dayId;
    private SimpleDateFormat dateSdf, timeSdf;
    private ProgressBar progress_bar;
    private Spinner spBkDate;
    private MyArrayAdapter bkDateAdapter;

    private List<String> bkTempList, bkPrevDate, bkTodayDate, bkAfterdate;

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
        /*Toolbar init start here*/
        if (getContext() instanceof MainActivity) {
            ((MainActivity) getContext()).setTitle(getString(R.string.title_bookings));
            ((MainActivity) getContext()).setBackButtonVisibility(8);
        }
        /*Toolbar init end here*/

        dateSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        timeSdf = new SimpleDateFormat("hh:mm a", Locale.US);
        // init calendar
        MyFlexibleCalendar viewCalendar = view.findViewById(R.id.calendar);
        Calendar cal = Calendar.getInstance();
        CalendarAdapter adapter = new CalendarAdapter(getContext(), cal);
        viewCalendar.setAdapter(adapter);
        progress_bar = view.findViewById(R.id.progress_bar);

        setCalenderClickListner(viewCalendar);

        selectedDate = CalanderUtils.formatDate(CalanderUtils.getCurrentDate(), Constants.SERVER_TIMESTAMP_FORMAT, Constants.SERVER_TIMESTAMP_FORMAT);
        dayId = cal.get(GregorianCalendar.DAY_OF_WEEK) - 2;

        tv_msg = view.findViewById(R.id.tv_msg);

        initSpinner(view);
        initBookingRecycler(view);
    }

    private void initBookingRecycler(View view) {
        RecyclerView rvBookings = view.findViewById(R.id.rvBookings);

        List<String> list = new ArrayList<>();
        BookingsAdapter bookingsAdapter = new BookingsAdapter(list, pos -> {

        });
        rvBookings.setAdapter(bookingsAdapter);
    }

    private void initSpinner(View view) {
        spBkDate = view.findViewById(R.id.spBkDate);

        PreRegistrationSession pSession = Mualab.getInstance().getBusinessProfileSession();
        //User user = Mualab.getInstance().getSessionManager().getUser();

        bkTempList = new ArrayList<>();
        bkPrevDate = new ArrayList<>();
        bkPrevDate.add("All Booking");
        bkPrevDate.add("Cancelled Booking");
        bkPrevDate.add("Complete Booking");

        bkTodayDate = new ArrayList<>();
        bkTodayDate.add("All Booking");
        bkTodayDate.add("Todays Booking");
        bkTodayDate.add("Cancelled Booking");
        bkTodayDate.add("Complete Booking");

        bkAfterdate = new ArrayList<>();
        bkAfterdate.add("All Booking");
        bkAfterdate.add("Cancelled Booking");
        bkAfterdate.add("Confirm Booking");

        bkTempList.addAll(bkTodayDate);
        MyArrayAdapter bkTypeAdapter, bkStaffAdapter;
        bkDateAdapter = new MyArrayAdapter(getBaseActivity(), bkTempList);
        spBkDate.setAdapter(bkDateAdapter);
        spBkDate.setSelection(1);  //default set Todays booking

        //1:  Incall , 2: Outcall , 3: Both
        String selectedCompType = pSession.getCurrentCompanyDetail().serviceTypeOfCompany;
        if (selectedCompType.equals("Both") || pSession.getServiceType() == 3 && selectedCompType.isEmpty()) {
            view.findViewById(R.id.rlBkType).setVisibility(View.VISIBLE);
            Spinner spBkType = view.findViewById(R.id.spBkType);

            List<String> bkType = new ArrayList<>();
            bkType.add("All Type");
            bkType.add("Incall");
            bkType.add("Outcall");

            bkTypeAdapter = new MyArrayAdapter(getBaseActivity(), bkType);
            spBkType.setAdapter(bkTypeAdapter);
        }

      /*  if (!user.businessType.equals("independent")) {
            Spinner spBkStaff = view.findViewById(R.id.spBkStaff);
            //spBkStaff.setVisibility(View.VISIBLE);

            List<String> bkStaff = new ArrayList<>();
            bkStaff.add("All Staff");
            bkStaff.add("Staff 1");
            bkStaff.add("Staff 2");

            bkStaffAdapter = new MyArrayAdapter(getBaseActivity(), bkStaff);
            spBkStaff.setAdapter(bkStaffAdapter);
        }*/
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Todo handle this
        //getDeviceLocation();
        //apiForGetFreeSlots();
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


                if (viewCalendar.isSelectedDay(day)) {
                    Calendar todayCal = Calendar.getInstance();
                    int cYear = todayCal.get(Calendar.YEAR);
                    int cMonth = todayCal.get(Calendar.MONTH) + 1;
                    int cDay = todayCal.get(Calendar.DAY_OF_MONTH);

                    int year = day.getYear();
                    int dayOfMonth = day.getDay();

                    if (year >= cYear && month >= cMonth) {
                        if (year == cYear && month == cMonth && dayOfMonth < cDay) {
                            AppLogger.i("Date Test", "can't select previous date");
                        } else {
                            apiForGetFreeSlots();
                        }
                    } else {
                        AppLogger.i("Date Test", "can't select previous date");
                    }
                }
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
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
    }


    private void apiForGetFreeSlots() {
        //Todo handle this
    }

    private void apiForBookingAction(final String type, final Bookings bookings, final String serviceId, final String subServiceId, final String artistServiceId) {
        //Todo handle this
    }
}
