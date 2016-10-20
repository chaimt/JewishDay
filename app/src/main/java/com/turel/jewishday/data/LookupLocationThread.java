package com.turel.jewishday.data;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.turel.jewishday.fragments.AddLocationFragment;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Haim.Turkel on 11/24/2015.
 */
public class LookupLocationThread extends AsyncTask<Void, Void,Pair<Double,Double>> {
    public static final String TAG = LookupLocationThread.class.getSimpleName();


    private String address = "";
    private FragmentActivity fragment;

    public LookupLocationThread(String address, FragmentActivity fragment){
        this.address = address;
        this.fragment = fragment;
    }

    @Override
    protected Pair<Double, Double> doInBackground(Void... params) {
        Geocoder geocoder = new Geocoder(fragment, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address,1);
            Log.i(TAG, addresses.toString());
            if (addresses.size() > 0) {
                Address addr = addresses.get(0);
                return new Pair<>(addr.getLongitude(),addr.getLatitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Pair<Double, Double> doubleDoublePair) {

        AddLocationFragment resultFrag = (AddLocationFragment) fragment.getSupportFragmentManager()
                .findFragmentByTag(AddLocationFragment.FragTag);

        if (doubleDoublePair==null) {
            Toast.makeText(fragment,
                    "No Address Found", Toast.LENGTH_SHORT).show();
        }

        if (resultFrag!=null && doubleDoublePair!=null ) {
            resultFrag.updateCordinates(doubleDoublePair,null);
        }
    }
}
