package com.turel.jewishday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.turel.jewishday.utils.AppSettings;

/**
 * Created by Haim.Turkel on 1/12/2016.
 */
public class DayEventNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        AppSettings.getInstance().updateLocalLanguage(context);
        AppSettings.getInstance().checkToDisplayDateNotification();
        AppSettings.getInstance().displayTimeNotifications();
        AppSettings.getInstance().setNextDayAlarm();
        AppSettings.getInstance().setNextTimeNotificaitons();
    }
}
