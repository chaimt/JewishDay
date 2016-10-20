package com.turel.jewishday.data;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Haim.Turkel on 11/2/2015.
 */
public class UpdateLocationCordinates extends AsyncTask<String, Void, List<AddressInfo>> {
    public static final String TAG = UpdateLocationCordinates.class.getSimpleName();

    private Context context;

    public UpdateLocationCordinates(Context context) {
        this.context = context;
    }

    @Override
    protected List<AddressInfo> doInBackground(String... params) {
        try {
            List<AddressInfo> list = new LinkedList<>();
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> address = geocoder.getFromLocationName(params[0], 1);
            if (address.size() > 0) {
                Address addr = address.get(0);
//                list.add(new AddressInfo("", params[0], addr.getLongitude(), addr.getLatitude()));
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}