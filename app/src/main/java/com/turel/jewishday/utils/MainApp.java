package com.turel.jewishday.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by CHAIMT on 10/30/2014.
 */
public class MainApp extends Application {
    private static MainApp instance;

    public static MainApp getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }


}
