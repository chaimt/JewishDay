package com.turel.jewishday.utils;

import android.os.AsyncTask;

/**
 * Created by Haim.Turkel on 2/14/2016.
 */
public class UpdateAlarm extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        AppSettings.getInstance().setNextTimeNotificaitons();
        return null;
    }
}
