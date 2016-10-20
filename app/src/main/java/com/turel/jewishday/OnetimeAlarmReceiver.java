package com.turel.jewishday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.turel.jewishday.utils.AppSettings;

public class OnetimeAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			AppSettings.getInstance().updateLocalLanguage(context);
			AppSettings.getInstance().checkToDisplayDateNotification();
			AppSettings.getInstance().setNextDayAlarm();
			AppSettings.getInstance().setNextTimeNotificaitons();
		} catch (Exception e) {
		}
	}
}
