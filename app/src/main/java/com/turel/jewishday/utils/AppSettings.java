package com.turel.jewishday.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.turel.jewishday.MainActivity;
import com.turel.jewishday.R;
import com.turel.jewishday.data.AddressInfo;
import com.turel.jewishday.fragments.NestedPreferenceFragment;
import com.turel.jewishday.fragments.PreferencesScreen;

import net.sourceforge.zmanim.ComplexZmanimCalendar;
import net.sourceforge.zmanim.hebrewcalendar.HebrewDateFormatter;
import net.sourceforge.zmanim.hebrewcalendar.JewishCalendar;
import net.sourceforge.zmanim.util.GeoLocation;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by CHAIMT on 10/30/2014.
 */
public class AppSettings {


    public static final String TAG = AppSettings.class.getSimpleName();

    public static final String ALARM_INTENT_SET_JEWISH_DATE = "SET_JEWISH_DATE";
    public static final String ALARM_INTENT_REMINDER = "JEWISH_REMINDER";
    public static final int CURRENT_LOCATION_INDEX = -1;

    public static final String ALARM_DESCRIPTION_TITLE = "ALARM_DESCRIPTION_TITLE";
    public static final String ALARM_DESCRIPTION_TEXT = "ALARM_DESCRIPTION_TEXT";
    public static final String REMINDER_USE = "reminder.use";


    private static AppSettings _instance;
    private boolean inIsrael;
    private boolean hebrewFormat = isLocaleHebrew;
    private TimeZone timeZone;

    private Context context;
    private double latitude = 32;
    private double longitude = 30;
    private double altitude = 300;
    private String address;
    private boolean displayNotification = true;
    private boolean displaySmartNotification = true;

    public static final String CHANNEL_ID = "DATE";
    protected static final int DISPLAY_DATE_ID = 1;
    public static final int DISPLAY_REMINDER_ID = 2;

    private java.text.DateFormat userTimeFormat = new SimpleDateFormat("HH:mm");

    public static AppSettings getInstance() {
        if (_instance == null) {
            _instance = new AppSettings(MainApp.getContext());
        }
        return _instance;
    }

    public Context getContext() {
        return context;
    }


    private AppSettings(Context context) {
        this.context = context;
        address = context.getString(R.string.address_unknown);

        loadSettings(context);
        createNotificationChannel();
    }

    public static boolean isLocaleHebrew = Locale.getDefault().getLanguage().equals("he") || Locale.getDefault().getLanguage().equals("iw");

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public String getAddress() {
        return address;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }


    private void loadSettings(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        inIsrael = prefs.getBoolean(PreferencesScreen.IN_ISRAEL_KEY, true);
        hebrewFormat = prefs.getBoolean(PreferencesScreen.IN_HEBREW_KEY, true) || "iw".equals(Locale.getDefault().getLanguage());

        String timezoneTxt = prefs.getString(PreferencesScreen.TIMEZONE_KEY, Calendar.getInstance().getTimeZone().getDisplayName());
        timeZone = TimeZone.getTimeZone(timezoneTxt);
        latitude = Utils.getDoublePreference(prefs, PreferencesScreen.TIMEZONE_KEY, latitude);
        longitude = Utils.getDoublePreference(prefs, PreferencesScreen.LONGITUDE_KEY, longitude);
        altitude = Utils.getDoublePreference(prefs, PreferencesScreen.ALTITUDE_KEY, altitude);
        address = prefs.getString(PreferencesScreen.ADDRESS_KEY, context.getString(R.string.address_unknown));
        displayNotification = prefs.getBoolean(PreferencesScreen.APPLICATION_NOTIFICATION_KEY, true);
        displaySmartNotification = prefs.getBoolean(PreferencesScreen.SMART_NOTIFICATION_KEY, true);

    }

    public Locale localeToUse() {
        return new Locale(hebrewFormat ? "iw" : "en");
    }

    public void updateLocalLanguage(Context context) {
        Locale.setDefault(localeToUse());
        Configuration config = new Configuration();
        config.locale = localeToUse();
        context.getResources().updateConfiguration(config, null);
    }

    public ComplexZmanimCalendar getZmanimData() {
        return getZmanimData(Calendar.getInstance());
    }

    public ComplexZmanimCalendar getZmanimData(String name, double latitude, double longitude, double elevation, TimeZone timeZone) {
        GeoLocation location = new GeoLocation(name, latitude, longitude, elevation, timeZone);
        ComplexZmanimCalendar zmanim = new ComplexZmanimCalendar(location);
        zmanim.setCalendar(Calendar.getInstance());
        return zmanim;
    }

    public ComplexZmanimCalendar getZmanimData(Calendar calendar) {
        String locationName = "Israel, Revava";
        GeoLocation location = new GeoLocation(locationName, latitude, longitude, altitude, timeZone);
        ComplexZmanimCalendar zmanim = new ComplexZmanimCalendar(location);
        zmanim.setCalendar(calendar);
        return zmanim;
    }

    public boolean isHebrewFormat() {
        return hebrewFormat;
    }

    public HebrewDateFormatter getHebrewDateFormatter() {
        HebrewDateFormatter formatter = new HebrewDateFormatter();
        formatter.setHebrewFormat(isHebrewFormat());
        return formatter;
    }

    public JewishCalendar getJewishCalendar() {
        return getJewishCalendar(null);
    }

    public JewishCalendar getJewishCalendarByTime(ComplexZmanimCalendar czc) {
        JewishCalendar jewishCalendar = getJewishCalendar();
        if (Utils.isBeforeNow(czc.getTzais())) {
            jewishCalendar.setDate(getNextDay(jewishCalendar));
        }
        return jewishCalendar;
    }

    public JewishCalendar getJewishCalendarByTime() {
        return getJewishCalendarByTime(getZmanimData());
    }

    public JewishCalendar getJewishCalendar(Calendar cal) {
        JewishCalendar jewishCalendar = (cal == null) ? new JewishCalendar() : new JewishCalendar(cal);
        jewishCalendar.setUseModernHolidays(true);
        jewishCalendar.setInIsrael(inIsrael);
        return jewishCalendar;
    }

    public void removeLocationData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        longitude = 0;
        latitude = 0;
        altitude = 0;
        editor.putString(PreferencesScreen.LATITUDE_KEY, "");
        editor.putString(PreferencesScreen.LONGITUDE_KEY, "");
        editor.putString(PreferencesScreen.ALTITUDE_KEY, "");
        editor.putString(PreferencesScreen.ADDRESS_KEY, context.getString(R.string.address_unknown));
        editor.apply();
    }

    public void updateAddress(String message) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PreferencesScreen.ADDRESS_KEY, message);
        address = message;
        editor.apply();

    }

    public void setNextDayAlarm() {
        //we need an alarm before tzais to update the date in gui
        ComplexZmanimCalendar czc = getZmanimData();
        if (Utils.isBeforeNow(czc.getTzais())) {
            Calendar cal = getNextDay(getJewishCalendar());
            czc = getZmanimData(cal);
        }
        long oneMinuteAfterTzais = czc.getTzais().getTime() + 1 * 60 * 1000;
        Intent alarmIntent = new Intent(ALARM_INTENT_SET_JEWISH_DATE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC, oneMinuteAfterTzais, AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    protected Calendar getNextDay(JewishCalendar jewishCalendar) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, jewishCalendar.getGregorianYear());
        cal.set(Calendar.MONTH, jewishCalendar.getGregorianMonth());
        cal.set(Calendar.DAY_OF_MONTH, jewishCalendar.getGregorianDayOfMonth());
        cal.add(Calendar.DATE, 1);
        return cal;
    }

    public void refreshDisplayDateNotification() {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(DISPLAY_DATE_ID);
        displayDateNotification();

    }

    public void checkToDisplayDateNotification() {
        if (!displayNotification) {
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(DISPLAY_DATE_ID);
            return;
        }

        displayDateNotification();
    }

    public void displayTimeNotifications() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean reminderUse = preferences.getBoolean(REMINDER_USE, false);
        if (reminderUse) {
            String title = preferences.getString(ALARM_DESCRIPTION_TITLE, "Jewish Day Alarm");
            String description = preferences.getString(ALARM_DESCRIPTION_TEXT, "Please set Alarms");

            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID).setSmallIcon(R.drawable.luach)
                    .setContentTitle(title).setContentText(description)
                    .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(false);
            NotificationManager mNotificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.notify(DISPLAY_REMINDER_ID, mBuilder.build());
        }
    }

    private TimeEstimation checkIfTimeRelavent(TimeEstimation nextTime, LocalTime requestNextTime, String description) {
        if (requestNextTime.isBefore(nextTime.getTommarowAlarm())) {
            nextTime.setTommarowAlarm(requestNextTime);
            nextTime.setTommarowDescription(description);
        }

        if (requestNextTime.isBefore(nextTime.getTodaysAlarm()) && requestNextTime.isAfter(LocalTime.now())) {
            nextTime.setTodaysAlarm(requestNextTime);
            nextTime.setTodaysDescription(description);
        }
        return nextTime;
    }

    private TimeEstimation getTime(SharedPreferences prefs,String switchKey, String timeKey, TimeEstimation nextTime, String defaultValue, String description) {
        Boolean switchStatus = prefs.getBoolean(switchKey, false);
        if (switchStatus) {
            String time = prefs.getString(timeKey, defaultValue);
            LocalTime localTime = new LocalTime(time);
            nextTime = checkIfTimeRelavent(nextTime,localTime,description);
        }
        return nextTime;
    }

    private TimeEstimation getDayEventTime(SharedPreferences prefs, String switchKey, TimeEstimation nextTime) {
        Boolean switchStatus = prefs.getBoolean(switchKey, false);
        if (switchStatus) {
            LocalTime eventTime = TimeEstimation.SEC_TO_MIDNIGHT;
            ComplexZmanimCalendar czc = getZmanimData();
            String description = "";
            switch (switchKey) {
                case NestedPreferenceFragment.DAY_SUNRISE_SWITCH:
                    eventTime = new LocalTime(czc.getSunrise());
                    description = context.getString(R.string.allot_hashchar_description);
                    break;
                case NestedPreferenceFragment.DAY_SCHARAIT_SWITCH:
                    eventTime = new LocalTime(czc.getSofZmanTfilaFixedLocal());
                    description = context.getString(R.string.shacarit_description);
                    break;
                case NestedPreferenceFragment.DAY_CHAZTOT_SWITCH:
                    eventTime = new LocalTime(czc.getChatzos());
                    description = context.getString(R.string.chaztot_description);
                    break;
                case NestedPreferenceFragment.DAY_SHEKIA_SWITCH:
                    eventTime = new LocalTime(czc.getSunset());
                    description = context.getString(R.string.shekia_description);
                    break;
                case NestedPreferenceFragment.DAY_TZET_SWITCH:
                    eventTime = new LocalTime(czc.getTzais());
                    description = context.getString(R.string.tzet_hacochavim_description);
                    break;
            }
            return checkIfTimeRelavent(nextTime, eventTime, description);
        }
        return nextTime;
    }


    public TimeEstimation getNextTimeNotificaitons() {
        TimeEstimation nextTime = new TimeEstimation();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        nextTime = getTime(prefs,NestedPreferenceFragment.SCHARAIT_SWITCH, NestedPreferenceFragment.SCHARAIT_TIME, nextTime, NestedPreferenceFragment.SCHARAIT_DEFAULT_TIME,context.getString(R.string.preferences_reminder_shacarit_text));
        nextTime = getTime(prefs,NestedPreferenceFragment.MINCHA_SWITCH, NestedPreferenceFragment.MINCHA_TIME, nextTime, NestedPreferenceFragment.MINCHA_DEFAULT_TIME,context.getString(R.string.preferences_reminder_mincha_text));
        nextTime = getTime(prefs,NestedPreferenceFragment.ARVIT_SWITCH, NestedPreferenceFragment.ARVIT_TIME, nextTime, NestedPreferenceFragment.ARVIT_DEFAULT_TIME,context.getString(R.string.preferences_reminder_arvit_text));

        nextTime = getDayEventTime(prefs,NestedPreferenceFragment.DAY_SUNRISE_SWITCH, nextTime);
        nextTime = getDayEventTime(prefs,NestedPreferenceFragment.DAY_SCHARAIT_SWITCH, nextTime);
        nextTime = getDayEventTime(prefs,NestedPreferenceFragment.DAY_CHAZTOT_SWITCH, nextTime);
        nextTime = getDayEventTime(prefs,NestedPreferenceFragment.DAY_SHEKIA_SWITCH, nextTime);
        nextTime = getDayEventTime(prefs,NestedPreferenceFragment.DAY_TZET_SWITCH, nextTime);

        return nextTime;
    }

    public void setNextTimeNotificaitons() {
        Intent alarmIntent = new Intent(ALARM_INTENT_REMINDER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean reminderUse = prefs.getBoolean(REMINDER_USE, false);
        if (reminderUse) {
            TimeEstimation nextTimeNotificaitons = getNextTimeNotificaitons();
            DateTime nextAlarm = nextTimeNotificaitons.getNextAlarm();
            if (nextAlarm != null) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor edit = preferences.edit();
                edit.putString(ALARM_DESCRIPTION_TITLE, context.getString(R.string.app_name));
                edit.putString(ALARM_DESCRIPTION_TEXT, nextTimeNotificaitons.getNextAlarmDescription());
                edit.commit();

                long alarmTime = nextAlarm.getMillis();
                alarmManager.setInexactRepeating(AlarmManager.RTC, alarmTime, AlarmManager.INTERVAL_DAY, pendingIntent);
            }
        }
    }

    public List<AddressInfo> getAddressInfoList() {
        List<AddressInfo> addressList = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String locations = preferences.getString(AddressInfo.LOCATIONS_KEY, "");
        try {
            addressList = AddressInfo.fromJson(locations);
        } catch (JSONException e) {
        }
        return addressList;
    }

    public AddressInfo getAddressInfo() {
        AddressInfo currentAddress = null;
        List<AddressInfo> addressList = new LinkedList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String locations = preferences.getString(AddressInfo.LOCATIONS_KEY, "");
        try {
            addressList = AddressInfo.fromJson(locations);
        } catch (JSONException e) {
        }
        int currentIndex = preferences.getInt(AddressInfo.LOCATIONS_INDEX_KEY, CURRENT_LOCATION_INDEX);
        if (currentIndex != CURRENT_LOCATION_INDEX && currentIndex >= addressList.size()) {
            currentIndex = CURRENT_LOCATION_INDEX;
        }
        if (currentIndex == CURRENT_LOCATION_INDEX) {
            currentAddress = new AddressInfo(getContext().getString(R.string.current_location), getAddress(), getLatitude(), getLongitude(), getAltitude(), getTimeZone());
        } else {
            currentAddress = addressList.get(currentIndex);
        }
        return currentAddress;
    }

    private void displayDate(RemoteViews remoteViews, int id, Date date) {
        if (date != null) {
            remoteViews.setTextViewText(id, userTimeFormat.format(date));
        } else {
            remoteViews.setTextViewText(id, "NaN");
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void displayDateNotification() {
        updateLocalLanguage(context);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AddressInfo addressInfo = getAddressInfo();
        HebrewDateFormatter formatter = getHebrewDateFormatter();
        ComplexZmanimCalendar czc = getZmanimData(addressInfo.getAddress(), addressInfo.getLatitude(), addressInfo.getLongitude(), addressInfo.getAltitude(), addressInfo.getZone());
        JewishCalendar jewishCalendar = getJewishCalendarByTime();

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notificationmain);
        remoteViews.setImageViewResource(R.id.notifimage, R.drawable.luach);
        remoteViews.setTextViewText(R.id.jdate, formatter.format(jewishCalendar));

        displayDate(remoteViews, R.id.sunrise, czc.getSunrise());
        remoteViews.setTextViewText(R.id.netzText, context.getString(R.string.netz_hachama_text));
        displayDate(remoteViews, R.id.sunset, czc.getSunset());
        remoteViews.setTextViewText(R.id.sunsetText, context.getString(R.string.shekia_description));
        displayDate(remoteViews, R.id.middle, czc.getChatzos());
        remoteViews.setTextViewText(R.id.chzotText, context.getString(R.string.chaztot_text));
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setTextColor(R.id.jdate, Color.WHITE);
            remoteViews.setTextColor(R.id.sunrise, Color.WHITE);
            remoteViews.setTextColor(R.id.sunset, Color.WHITE);
            remoteViews.setTextColor(R.id.middle, Color.WHITE);
        }

        int monthPos = R.drawable.ic_month_01;
        //smart display does not show month name after day 4
        if (displaySmartNotification && jewishCalendar.getJewishDayOfMonth() >= 4) {
            monthPos = R.drawable.ic_month_00;
        } else {
            boolean isLeapYear = jewishCalendar.isJewishLeapYear();
            if (isLeapYear && jewishCalendar.getJewishMonth()>=12){
                monthPos = monthPos + jewishCalendar.getJewishMonth();
            }
            else {
                monthPos = monthPos + jewishCalendar.getJewishMonth() - 1;
            }
        }

        if (monthPos == R.drawable.ic_month_00){
            monthPos = R.drawable.ic_day_01 + jewishCalendar.getJewishDayOfMonth()-1;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(monthPos,jewishCalendar.getJewishDayOfMonth())
                .setContent(remoteViews)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setOngoing(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notif = mBuilder.build();
        //notif.iconLevel = jewishCalendar.getJewishDayOfMonth();
        mNotificationManager.notify(DISPLAY_DATE_ID, notif);
    }

    public void setDisplayNotification(boolean displayNotification) {
        this.displayNotification = displayNotification;
    }

    public void setDisplaySmartNotification(boolean displaySmartNotification) {
        this.displaySmartNotification = displaySmartNotification;
    }

    public void setLocation(double currentLatitude, double currentLongitude, double currentAltitude) {
        latitude = currentLatitude;
        longitude = currentLongitude;
        altitude = currentAltitude;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PreferencesScreen.LATITUDE_KEY, String.valueOf(latitude));
        editor.putString(PreferencesScreen.LONGITUDE_KEY, String.valueOf(longitude));
        editor.putString(PreferencesScreen.ALTITUDE_KEY, String.valueOf(altitude));
        editor.putString(PreferencesScreen.ADDRESS_KEY, context.getString(R.string.address_unknown));
        editor.apply();
    }

}
