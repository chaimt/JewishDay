package com.turel.jewishday.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.turel.jewishday.R;
import com.turel.jewishday.data.AddressInfo;
import com.turel.jewishday.utils.AppSettings;
import com.turel.jewishday.utils.Utils;

import net.sourceforge.zmanim.util.GeoLocation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardViewNative;

/**
 * Created by Haim.Turkel on 11/2/2015.
 */
public class AddLocationFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    private enum ErrorType{
        ok,longitude,latitude,timezone,title,address,global
    }

    public static final String FragTag = "addLocation";
    private static final String TAG = "AddLocationFragment";

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    public static Fragment newInstance() {
        return new AddLocationFragment();
    }


    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private EditText titleView;
    private EditText longView;
    private EditText latView;
    private EditText timezoneView;

    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
        rootView = inflater.inflate(R.layout.fragment_add_location, container,
                false);

        AppSettings.getInstance().updateLocalLanguage(getActivity());

        titleView = (EditText) rootView.findViewById(R.id.title);
        mAutocompleteView = (AutoCompleteTextView)
                rootView.findViewById(R.id.autocomplete_places);

        // Register a listener that receives callbacks when a suggestion has been selected
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String locations = preferences.getString(AddressInfo.LOCATIONS_KEY, "");
                ErrorType errorPos = ErrorType.ok;
                try {
                    errorPos = ErrorType.longitude;
                    double lon = Double.valueOf(longView.getText().toString());
                    errorPos = ErrorType.latitude;
                    double lat = Double.valueOf(latView.getText().toString());;
                    errorPos = ErrorType.timezone;
                    if (timezoneView.getText().toString().trim().length()==0){
                        throw new Exception("timezone cannot be empty");
                    }
                    TimeZone timeZone = TimeZone.getTimeZone(timezoneView.getText().toString());

                    errorPos = ErrorType.global;
                    AddressInfo addressInfo = new AddressInfo(titleView.getText().toString().trim(), mAutocompleteView.getText().toString(), lon, lat,timeZone);
                    //validate information
                    new GeoLocation("", lat, lon, 0, timeZone);

                    errorPos = ErrorType.address;
                    if (addressInfo.getAddress()==null || addressInfo.getAddress().trim().length()==0){
                        throw new Exception("Address must be entered");
                    }

                    errorPos = ErrorType.title;
                    if (addressInfo.getTitle()==null || addressInfo.getTitle().trim().length()==0){
                        throw new Exception("Title must be entered");
                    }

                    List<AddressInfo> list = new ArrayList<>();
                    if (locations.trim().length()>0){
                        list = AddressInfo.fromJson(locations);
                    }
                    list.add(addressInfo);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(AddressInfo.LOCATIONS_KEY, AddressInfo.toJson(list));
                    editor.apply();
                    mGoogleApiClient.disconnect();
                    getFragmentManager()
                            .beginTransaction().replace(R.id.container,
                            new LocationsFragment())
                            .commit();

                } catch (Exception e) {
                    if (errorPos!=ErrorType.ok) {
                        String errorHeader = "";
                        switch (errorPos){
                            case longitude:
                                errorHeader = getString(R.string.error_longitude);
                                break;
                            case latitude:
                                errorHeader = getString(R.string.error_latitude);
                                break;
                            case timezone:
                                errorHeader = getString(R.string.error_timezone);
                                break;
                            case title:
                                errorHeader = getString(R.string.error_title);
                                break;
                            case address:
                                errorHeader = getString(R.string.error_address);
                                break;
                            case global:
                                errorHeader = getString(R.string.error_global);
                                break;
                        }
                        Toast.makeText(getContext(),
                                errorHeader + " - " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(),
                                "Error " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });


        longView = (EditText) rootView.findViewById(R.id.longitude);
        latView = (EditText) rootView.findViewById(R.id.latitude);
        timezoneView = (EditText) rootView.findViewById(R.id.timezone);

        Switch switchView = (Switch) rootView.findViewById(R.id.switch1);
        longView.setEnabled(switchView.isChecked());
        latView.setEnabled(switchView.isChecked());
        timezoneView.setEnabled(switchView.isChecked());
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                longView.setEnabled(isChecked);
                latView.setEnabled(isChecked);
                timezoneView.setEnabled(isChecked);

            }
        });



        return rootView;
    }

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            if (titleView.getText().toString().trim().length() == 0) {
                titleView.setText(primaryText);
            }

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            final Calendar cal = Calendar.getInstance(place.getLocale());
            TimeZone timeZone = cal.getTimeZone();
            updateCordinates(new Pair<Double, Double>(place.getLatLng().latitude, place.getLatLng().longitude),timeZone);

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getContext(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init_simple_card();
    }

    private void init_simple_card() {

        //Create a Card
        Card card = new Card(getActivity());

        //Create a CardHeader
        CardHeader header = new CardHeader(getActivity());

        card.addCardHeader(header);

        //Set card in the cardView
        CardViewNative cardView = (CardViewNative) getActivity().findViewById(R.id.card_add_location);
        cardView.setCard(card);
    }


    public void updateCordinates(Pair<Double, Double> doubleDoublePair, TimeZone timeZone) {
        longView.setText(String.valueOf(Utils.truncateExponent(doubleDoublePair.first)));

        latView.setText(String.valueOf(Utils.truncateExponent(doubleDoublePair.second)));
        timezoneView.setText(timeZone.getID());
    }

    @Override
    public void onPause() {
        mGoogleApiClient.disconnect();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }
}
