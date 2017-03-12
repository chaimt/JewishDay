package com.turel.jewishday.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.turel.jewishday.R;
import com.turel.jewishday.utils.AppSettings;
import com.turel.jewishday.utils.TimePreference;
import com.turel.jewishday.utils.UpdateAlarm;

/**
 * Created by Haim.Turkel on 1/9/2016.
 */
public class NestedPreferenceFragment extends PreferenceFragment {

    public static final int NESTED_SCREEN_REMIND_DAV = 1;
    public static final int NESTED_SCREEN_REMIND_DAY = 2;
    public static final int NESTED_SCREEN_DAY_SETTINGS = 3;

    private static final String TAG_KEY = "NESTED_KEY";

    public static final String SCHARAIT_SWITCH = "reminder.shacarit";
    public static final String SCHARAIT_TIME = "reminder.shacarit.time";
    public static final String SCHARAIT_DEFAULT_TIME = "7:00";
    public static final String MINCHA_SWITCH = "reminder.mincha";
    public static final String MINCHA_TIME = "reminder.mincha.time";
    public static final String MINCHA_DEFAULT_TIME = "14:00";
    public static final String ARVIT_SWITCH = "reminder.arvit";
    public static final String ARVIT_TIME = "reminder.arvit.time";
    public static final String ARVIT_DEFAULT_TIME = "21:00";

    public static final String DAY_SUNRISE_SWITCH = "reminder.day.sunrise";
    public static final String DAY_SCHARAIT_SWITCH = "reminder.day.shacarit";
    public static final String DAY_CHAZTOT_SWITCH = "reminder.day.chazot";
    public static final String DAY_SHEKIA_SWITCH = "reminder.day.shekia";
    public static final String DAY_TZET_SWITCH = "reminder.day.tzet.hacochavim";


    public static NestedPreferenceFragment newInstance(int key) {
        NestedPreferenceFragment fragment = new NestedPreferenceFragment();
        // supply arguments to bundle.
        Bundle args = new Bundle();
        args.putInt(TAG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        checkPreferenceResource();

    }

    public void setupSwicth(String switchKey){
        final SwitchPreference prefSwitch = (SwitchPreference)findPreference(switchKey);
        if (prefSwitch!=null) {
            prefSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    new UpdateAlarm().execute();
//                    AppSettings.getInstance().setNextTimeNotificaitons();
                    return true;
                }
            });
        }
    }

    public void setupTimePicker(SharedPreferences prefs, String switchKey, String timeKey, String defualtValue){
        final SwitchPreference prefSwitch = (SwitchPreference)findPreference(switchKey);
        if (prefSwitch!=null) {
            final Preference prefTimer = findPreference(timeKey);

            prefSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    prefTimer.setEnabled((Boolean) newValue);
                    new UpdateAlarm().execute();
//                    AppSettings.getInstance().setNextTimeNotificaitons();
                    return true;
                }
            });


            prefTimer.setEnabled(prefSwitch.isChecked());
            prefTimer.setSummary(prefs.getString(timeKey, defualtValue));
            prefTimer.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (preference instanceof TimePreference) {
                        preference.setSummary((String) newValue);
                        new UpdateAlarm().execute();
//                        AppSettings.getInstance().setNextTimeNotificaitons();
                    }
                    return true;
                }
            });
        }
    }

    private void checkPreferenceResource() {
        int key = getArguments().getInt(TAG_KEY);
        // Load the preferences from an XML resource
        switch (key) {
            case NESTED_SCREEN_REMIND_DAV:
                addPreferencesFromResource(R.layout.fragment_davining_settings);
                break;

            case NESTED_SCREEN_REMIND_DAY:
                addPreferencesFromResource(R.layout.fragment_day_settings);
                break;

            case NESTED_SCREEN_DAY_SETTINGS:
                addPreferencesFromResource(R.layout.fragment_day_time_settings);
                break;

            default:
                break;
        }
        if (key==NESTED_SCREEN_REMIND_DAV || key==NESTED_SCREEN_REMIND_DAY) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            setupTimePicker(prefs, SCHARAIT_SWITCH, SCHARAIT_TIME, "7:00");
            setupTimePicker(prefs, MINCHA_SWITCH, MINCHA_TIME, "14:00");
            setupTimePicker(prefs, ARVIT_SWITCH, ARVIT_TIME, "21:00");

            setupSwicth(DAY_SUNRISE_SWITCH);
            setupSwicth(DAY_SCHARAIT_SWITCH);
            setupSwicth(DAY_CHAZTOT_SWITCH);
            setupSwicth(DAY_SHEKIA_SWITCH);
            setupSwicth(DAY_TZET_SWITCH);
        }

    }

}