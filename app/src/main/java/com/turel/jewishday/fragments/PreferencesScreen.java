package com.turel.jewishday.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.turel.jewishday.R;
import com.turel.jewishday.utils.AppSettings;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

public class PreferencesScreen extends PreferenceFragment  implements  Preference.OnPreferenceClickListener{

    private static final String KEY_REMIND_DAV = "KEY_REMIND_DAV";
    private static final String KEY_REMIND_DAY = "KEY_REMIND_DAY";

    private static final String TAG_NESTED = "TAG_NESTED";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // here you should use the same keys as you used in the xml-file
        if (preference.getKey().equals(KEY_REMIND_DAV)) {
            getActivity().
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.container, NestedPreferenceFragment.newInstance(NestedPreferenceFragment.NESTED_SCREEN_REMIND_DAV), TAG_NESTED).
                    addToBackStack(null).
                    commit();

        }
        else if (preference.getKey().equals(KEY_REMIND_DAY)) {
            getActivity().
                    getSupportFragmentManager().beginTransaction().
                    replace(R.id.container, NestedPreferenceFragment.newInstance(NestedPreferenceFragment.NESTED_SCREEN_REMIND_DAY), TAG_NESTED).
                    addToBackStack(null).
                    commit();
        }

        return false;

    }


    public static final String APPLICATION_NOTIFICATION_KEY = "application.notification";
    public static final String SMART_NOTIFICATION_KEY = "smart.notification";
    public static final String IN_ISRAEL_KEY = "location.israel";
    public static final String IN_HEBREW_KEY = "in.hebrew";
    public static final String LATITUDE_KEY = "latitude";
    public static final String LONGITUDE_KEY = "longitude";
    public static final String TIMEZONE_KEY = "timezone";
    public static final String DEFAULT_TIMEZONE = "Israel Standard Time";
    public static final String ALTITUDE_KEY = "altitude";
    public static final String ADDRESS_KEY = "address";
    public static final String NEXT_ALARM_KEY = "key.next.alarm";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setTitle(R.string.menu_title);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.layout.fragment_settings);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        addValueToTitle(prefs.getString(LATITUDE_KEY, ""), LATITUDE_KEY);
        addValueToTitle(prefs.getString(LONGITUDE_KEY, ""), LONGITUDE_KEY);
        addValueToTitle(prefs.getString(TIMEZONE_KEY, DEFAULT_TIMEZONE), TIMEZONE_KEY);
        addValueToTitle(prefs.getString(ALTITUDE_KEY, ""), ALTITUDE_KEY);


        Preference pref = findPreference(IN_HEBREW_KEY);
        if (AppSettings.isLocaleHebrew) {
            pref.setEnabled(false);
            ((CheckBoxPreference) pref).setChecked(true);
        } else {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Toast.makeText(getActivity(), getString(R.string.preferences_reset_app), Toast.LENGTH_LONG).show();
                    return true;
                }
            });
        }

        pref = findPreference(APPLICATION_NOTIFICATION_KEY);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean display = (Boolean) newValue;
                AppSettings.getInstance().setDisplayNotification(display);
                updatePerfrences();
                return true;
            }
        });
        pref = findPreference(SMART_NOTIFICATION_KEY);
        pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean display = (Boolean) newValue;
                AppSettings.getInstance().setDisplaySmartNotification(display);
                updatePerfrences();
                return true;
            }
        });

        Preference preference = findPreference("reminder.use");
        boolean remindUse = ((SwitchPreference)preference).isChecked();

        final Preference preferenceDav = findPreference(KEY_REMIND_DAV);
        preferenceDav.setOnPreferenceClickListener(this);
        preferenceDav.setEnabled(remindUse);

        final Preference preferenceDay = findPreference(KEY_REMIND_DAY);
        preferenceDay.setOnPreferenceClickListener(this);
        preferenceDay.setEnabled(remindUse);


        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean display = (Boolean) newValue;
                preferenceDay.setEnabled(display);
                preferenceDav.setEnabled(display);
                return true;
            }
        });
    }


    private void updatePerfrences(){
        AppSettings.getInstance().checkToDisplayDateNotification();

    }

    @Override
    public void onResume() {
        super.onResume();
        Preference preference = findPreference(NEXT_ALARM_KEY);
        DateTime nextAlarm = AppSettings.getInstance().getNextTimeNotificaitons().getNextAlarm();

        String time = nextAlarm==null ? "" : "-" + simpleDateFormat.format(nextAlarm.toDate());
        preference.setSummary(AppSettings.getInstance().getNextTimeNotificaitons().getNextAlarmDescription() + time);
    }

    private void addValueToTitle(String value, String prefName) {
        if (!TextUtils.isEmpty(value)) {
            Preference preferenceScreen = findPreference(prefName);
            if (preferenceScreen != null) {
                StringBuilder rackingSystemSb = new StringBuilder(prefName.toLowerCase());
                rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
                prefName = rackingSystemSb.toString();
                preferenceScreen.setTitle(String.format("%s (%s)", prefName, value));
            }
        }

        this.getActivity();
    }


}
