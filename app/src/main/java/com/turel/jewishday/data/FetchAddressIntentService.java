package com.turel.jewishday.data;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.turel.jewishday.R;
import com.turel.jewishday.utils.AppSettings;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Haim.Turkel on 7/17/2015.
 */
public class FetchAddressIntentService extends IntentService {
//    public static final int SUCCESS_RESULT = 0;
//    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
//    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
//    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
//            ".RESULT_DATA_KEY";
//    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
//            ".LOCATION_DATA_EXTRA";

    public static final String TAG = FetchAddressIntentService.class.getSimpleName();
//    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(AppSettings.getInstance().getLatitude(), AppSettings.getInstance().getLongitude(), 2);
            Log.i(TAG, address.toString());
            if (address.size() > 0) {
                Address addr = address.get(0);
                String addrStr = String.format("%s %s, %s", addr.getThoroughfare() != null ? addr.getThoroughfare() : "", addr.getSubThoroughfare() != null ? addr.getSubThoroughfare() : "", addr.getLocality()!=null ? addr.getLocality() : "");
                AppSettings.getInstance().updateAddress(addrStr);
            } else {
                AppSettings.getInstance().updateAddress(getApplication().getApplicationContext().getString(R.string.address_none));
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
    }
}
