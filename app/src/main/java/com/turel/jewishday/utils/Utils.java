package com.turel.jewishday.utils;

import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    //	public static final String TIME_24_FORMAT = "kk:mm";
//	public static final String TIME_AM_PM_FORMAT = "hh:mm a";
    private static final DecimalFormat twoDForm = new DecimalFormat("#.##");



    public static Float tryParse(String value, Float def) {
        try {
            return Float.parseFloat(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static Integer tryParse(String value, Integer def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return def;
        }
    }

    public static Date tryParse(java.text.DateFormat userTimeFormat, String value, Date def) {
        try {
            return userTimeFormat.parse(value);
        } catch (ParseException e) {
            return def;
        }
    }

    public static boolean isAfterNow(Date time) {
        Date now = new Date();
        time.setYear(now.getYear());
        time.setMonth(now.getMonth());
        time.setDate(now.getDate());
        return now.after(time);
    }

    public static boolean isBeforeNow(Date time) {
        Date now = new Date();
        time.setYear(now.getYear());
        time.setMonth(now.getMonth());
        time.setDate(now.getDate());
        return time.before(now);
    }
    public static float truncateExponent(float value) {
        return Float.valueOf(twoDForm.format(value));
    }

    public static double truncateExponent(double value) {
        return Double.valueOf(twoDForm.format(value));
    }
    public static Date Now(){return new Date();}

    public static Date truncate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Boolean passed(Date a, Date b, long milliDiff){
        return a.getTime() - b.getTime() > milliDiff;
    }

    public static Date getTime(int hourOfDay, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE,0);
        cal.set(Calendar.DAY_OF_MONTH,0);
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }



    public static int getIntPreference(SharedPreferences preferences,String key, int defValue){
        String value = preferences.getString(key, null);
        return value == null ? defValue : Integer.valueOf(value);
    }

    public static double getDoublePreference(SharedPreferences preferences,String key, double defValue){
        String value = preferences.getString(key, null);
        return (value == null || value.trim().equals("")) ? defValue : Double.valueOf(value);
    }

}
