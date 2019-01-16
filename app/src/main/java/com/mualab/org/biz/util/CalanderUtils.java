package com.mualab.org.biz.util;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by dharmraj on 6/2/18.
 **/

@SuppressLint("WrongConstant")
public class CalanderUtils {

    public static Date amPmTo24hour(String time ) {
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        String time24hourStr;
        Date time24hour = null;
        try {
            time24hour = date24Format.parse(time);
            time24hourStr = date24Format.format(date12Format.parse(time));
            System.out.println(time24hour);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time24hour;
    }

    public static int getDayShift(Calendar calendar, boolean z) {
        int i = 0;
        Calendar calendar2 = (Calendar) calendar.clone();
        calendar2.setMinimalDaysInFirstWeek(1);
        calendar2.set(5, 1);
        int i2 = calendar2.get(7);
        int firstDayOfWeek = calendar2.getFirstDayOfWeek();
        if (i2 != firstDayOfWeek) {
            if (i2 > firstDayOfWeek) {
                i = i2 - firstDayOfWeek;
            } else if (i2 < firstDayOfWeek) {
                i = (i2 + 7) - firstDayOfWeek;
            }
        }
        if (z) {
            return (calendar2.getActualMaximum(5) + i) % 7;
        }
        return i;
    }

    public static List<Calendar> getCalendarStartAndEndDateForDesiredPeriod(Calendar calendar) {
        Calendar calendar2 = (Calendar) calendar.clone();
        calendar2.setMinimalDaysInFirstWeek(1);
        int actualMaximum = calendar2.getActualMaximum(4) * 7;
        calendar2.set(5, 1);
        calendar2.add(5, -getDayShift(calendar2, false));
        List<Calendar> arrayList = new ArrayList();
        arrayList.add((Calendar) calendar2.clone());
        calendar2.add(5, actualMaximum);
        arrayList.add((Calendar) calendar2.clone());
        return arrayList;
    }

    public static int getMonthDifference(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            return 0;
        }
        int i = calendar.get(1);
        return (((i - calendar2.get(1)) * 12) + calendar.get(2)) - calendar2.get(2);
    }

    public static boolean isSameMonth(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            return false;
        }
        int i = calendar.get(2);
        int i2 = calendar2.get(2);
        int i3 = calendar.get(1);
        int i4 = calendar2.get(1);
        return i == i2 && i3 == i4;
    }

    public static boolean isSameDay(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            return false;
        }
        int i = calendar.get(1);
        int i2 = calendar.get(2);
        int i3 = calendar.get(5);
        int i4 = calendar2.get(1);
        int i5 = calendar2.get(2);
        int i6 = calendar2.get(5);
        return i == i4 && i2 == i5 && i3 == i6;
    }

    public static boolean isToday(Calendar calendar) {
        return isSameDay(Calendar.getInstance(), calendar);
    }

    public static boolean isYesterday(Calendar calendar) {
        Calendar instance = Calendar.getInstance();
        instance.add(5, -1);
        return isSameDay(instance, calendar);
    }

    public static boolean isSameHour(Calendar calendar, Calendar calendar2) {
        if (calendar == null || calendar2 == null) {
            return false;
        }
        int i = calendar.get(11);
        int i2 = calendar.get(12);
        int i3 = calendar2.get(11);
        int i4 = calendar2.get(12);
        return i == i3 && i2 == i4;
    }

    public static int compareDateWithoutTime(Date date, Date date2) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        Calendar instance2 = Calendar.getInstance();
        instance2.setTime(date2);
        return compareDateWithoutTime(instance, instance2);
    }

    public static int compareDateWithoutTime(Calendar calendar, Calendar calendar2) {
        int i = calendar.get(1);
        int i2 = calendar2.get(1);
        if (i < i2) {
            return -1;
        }
        if (i > i2) {
            return 1;
        }
        i = calendar.get(2);
        i2 = calendar2.get(2);
        if (i < i2) {
            return -1;
        }
        if (i > i2) {
            return 1;
        }
        i = calendar.get(5);
        i2 = calendar2.get(5);
        if (i < i2) {
            return -1;
        }
        if (i > i2) {
            return 1;
        }
        return 0;
    }

    public static String getTimestamp(String format) {
        return new SimpleDateFormat(format, Locale.US).format(new Date());
    }

    public static String getTimestamp() {
        return String.valueOf(new Date().getTime());
    }

    public static String format12HourTime(String time, @NonNull String pFormat, @NonNull String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(pFormat, Locale.US);
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.US);
            Date dTime = parseFormat.parse(time);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatDate(String date, @NonNull String pFormat, @NonNull String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(pFormat, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.getDefault());
            Date dTime = parseFormat.parse(date);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String dateFormatToShow(String date, @NonNull String pFormat, @NonNull String dFormat) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(pFormat, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(dFormat, Locale.getDefault());
            Date dTime = parseFormat.parse(date);
            return displayFormat.format(dTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Date getDateFormat(String date, @NonNull String format) {
        try {
            SimpleDateFormat parseFormat = new SimpleDateFormat(format, Locale.getDefault());

            return parseFormat.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @NonNull
    public static String getCurrentDate() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        return (year + "-" + month + "-" + day);
    }

    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("hh:mm a", Locale.US);
        System.out.println("currentTime" + date.format(currentLocalTime));
        return date.format(currentLocalTime);
    }

    private Calendar setCalendar(Time time) {

        /**/

        Calendar c = Calendar.getInstance();
        c.set(Calendar.AM_PM, time.AM_PM);
        c.set(Calendar.HOUR, time.HOUR);
        c.set(Calendar.MINUTE, time.MINUTE);
        c.set(Calendar.SECOND, 00);
        c.set(Calendar.MILLISECOND, 00);

        GregorianCalendar calendar = (GregorianCalendar) c;
        calendar.set(Calendar.AM_PM, time.AM_PM);
        calendar.set(Calendar.HOUR, time.HOUR);
        calendar.set(Calendar.MINUTE, time.MINUTE);
        calendar.set(Calendar.SECOND, 00);
        calendar.set(Calendar.MILLISECOND, 00);

        /* Calendar calendar1 = setCalendar(new Time(startTime));
            Calendar calendar2 = setCalendar(new Time(endTime));
            Calendar calendar3 = setCalendar(new Time(sTime1));
            Calendar calendar4 = setCalendar(new Time(eTime2));

           // boolean afterTime = x.after(calendar1.getTime()) || x.equals(calendar1.getTime());
           // boolean beforTime = x.before(calendar2.getTime()) || x.equals(calendar2.getTime());

           // int from = calendar1.get(Calendar.HOUR_OF_DAY) * 100 + calendar1.get(Calendar.MINUTE);
            int from = calendar1.get(Calendar.HOUR_OF_DAY);
            int to = calendar2.get(Calendar.HOUR_OF_DAY);
            int t = calendar3.get(Calendar.HOUR_OF_DAY);
            int am_pm1 = calendar1.get(Calendar.AM_PM); // 0 for am and 1 for PM
            int am_pm2 = calendar2.get(Calendar.AM_PM); // 0 for am and 1 for PM
            int am_pm3 = calendar3.get(Calendar.AM_PM);// 0 for am and 1 for PM
            int am_pm4 = calendar4.get(Calendar.AM_PM);// 0 for am and 1 for PM
*/
        return calendar;
    }

    public boolean compareTime(String startTime, String endTime, String sTime1) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("hh:mm aa");
            Date date1 = format.parse(startTime);
            Date date2 = format.parse(endTime);
            Date date3 = format.parse(sTime1);
            //Date date4 = format.parse(eTime2);

            if (date3.equals(date1) || date3.equals(date2)) {
                return true;
            } else if (date3.after(date1) && date3.before(date2)) {
                return true;
            } else if (date3.before(date1) || date3.before(date2))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private class Time {
        private int HOUR;
        private int MINUTE;
        private int AM_PM; //0-1

        private Time(String time) {

            String newTime;
            String timeArray[];

            if (time.contains("AM") || time.contains("am")) {
                AM_PM = Calendar.AM;
                newTime = time.replace("AM", "");
                newTime = newTime.replace("am", "");
            } else {
                AM_PM = Calendar.PM;
                newTime = time.replace("PM", "");
                newTime = newTime.replace("pm", "");
            }
            newTime = newTime.trim();
            timeArray = newTime.split(":");

            HOUR = Integer.parseInt(timeArray[0]);
            MINUTE = Integer.parseInt(timeArray[1]);
        }
    }
}
