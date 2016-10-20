package com.turel.jewishday.fragments;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.turel.jewishday.R;
import com.turel.jewishday.utils.AppSettings;
import com.turel.jewishday.utils.LocationUtils;
import com.turel.jewishday.utils.Utils;

import net.sourceforge.zmanim.util.GeoLocation;
import net.sourceforge.zmanim.util.GeoLocationUtils;

import java.util.TimeZone;

/**
 * Created by Haim.Turkel on 8/4/2015.
 */
public class KotelFragment extends Fragment implements SensorEventListener {


    // define the display assembly compass picture
    private ImageView image;
    private ImageView arrow;
    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    private TextView tvHeading;
    private TextView jslDistance;
    private TextView jslHeading;
    private double distanceToJerusalem;
    private double bearingToJerusalem;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kotel, container,
                false);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setTitle(R.string.menu_kotel);

        image = (ImageView) rootView.findViewById(R.id.imageViewCompass);
        arrow = (ImageView) rootView.findViewById(R.id.imageViewArrow);
        // TextView that will tell the user what degree is he heading
        tvHeading = (TextView) rootView.findViewById(R.id.tvHeading);
        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);

        GeoLocation here = new GeoLocation("here", AppSettings.getInstance().getLatitude(), AppSettings.getInstance().getLongitude(), TimeZone.getDefault());
        GeoLocation jerusalem = new GeoLocation("jerusalem", LocationUtils.HA_KOTEL_LAT, LocationUtils.HA_KOTEL_LON, TimeZone.getDefault());
        distanceToJerusalem = Utils.truncateExponent(GeoLocationUtils.getGeodesicDistance(here, jerusalem) / 1000);
        bearingToJerusalem = Utils.truncateExponent(GeoLocationUtils.getGeodesicFinalBearing(here, jerusalem));

        jslDistance = (TextView) rootView.findViewById(R.id.jslDistance);

        jslDistance.setText(String.format(getActivity().getString(R.string.jsl_distance), distanceToJerusalem));
        jslHeading = (TextView) rootView.findViewById(R.id.jslHeading);
        jslHeading.setText(String.format(getActivity().getString(R.string.jsl_heading), bearingToJerusalem));

        return rootView;
    }

    public static Fragment newInstance() {
        return new KotelFragment();
    }


    @Override
    public void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onPause() {
        super.onPause();
        // to stop the listener and save battery
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        int arrowId;
        if (Math.abs(degree - bearingToJerusalem) < 5) {
            arrowId = R.drawable.final_state;
        } else {
            arrowId = degree > bearingToJerusalem ? R.drawable.arrow_left : R.drawable.arrow_right;
        }


        arrow.setImageDrawable(getActivity().getResources().getDrawable(arrowId));

        tvHeading.setText(String.format(getActivity().getString(R.string.global_heading), degree));

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);
        // Start the animation
        image.startAnimation(ra);
        currentDegree = -degree;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
}
