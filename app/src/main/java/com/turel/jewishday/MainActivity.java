package com.turel.jewishday;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.turel.jewishday.data.FetchAddressIntentService;
import com.turel.jewishday.fragments.AboutFragment;
import com.turel.jewishday.fragments.KotelFragment;
import com.turel.jewishday.fragments.LocationsFragment;
import com.turel.jewishday.fragments.PreferencesScreen;
import com.turel.jewishday.fragments.ZmanimFragment;
import com.turel.jewishday.utils.AppSettings;
import com.turel.jewishday.utils.LocationUtils;
import com.turel.jewishday.utils.PermissionManager;
import com.turel.jewishday.utils.Utils;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final String TAG = MainActivity.class.getSimpleName();

    private final static int ZMANIM_POSITION = 0;
    private final static int KOTEL_POSITION = 1;
    private final static int LOCATIONS_POSITION = 2;
    private final static int SETTINGS_POSITION = 3;
    private final static int ABOUT_POSITION = 4;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    private int currentPos = -1;

    private boolean checkingPremissions = false;

    private static final int REQUEST_LOCATION = 2;
    private static final int REQUEST_LATEST_LOCATION = 3;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean firstRequest = false;

    private void setupShortcuts() {
        if (android.os.Build.VERSION.SDK_INT >= 25) {
            ShortcutManager shortcutManager = this.getSystemService(ShortcutManager.class);

//            if (shortcutManager.getDynamicShortcuts().size() == 0) {

                final Intent berachotIntent = new Intent(this, MainActivity.class);
                berachotIntent.setAction(Intent.ACTION_VIEW);
                berachotIntent.putExtra("fragment", "Kotel");


                ShortcutInfo shortcutMizrach = new ShortcutInfo.Builder(this, "Kotel")
                        .setShortLabel(getString(R.string.kotel_shortcutShortLabel))
                        .setLongLabel(getString(R.string.kotel_shortcutLongLabel))
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_action_compass))
                        .setIntent(berachotIntent)
                        .build();

                final Intent zmanimIntent = new Intent(this, MainActivity.class);
                zmanimIntent.setAction(Intent.ACTION_VIEW);
                zmanimIntent.putExtra("fragment", "ZmanimFragment");

                ShortcutInfo shortcutZmanim = new ShortcutInfo.Builder(this, "ZmanimFragment")
                        .setShortLabel(getString(R.string.zmanim_shortcutShortLabel))
                        .setLongLabel(getString(R.string.zmanim_shortcutLongLabel))
                        .setIcon(Icon.createWithResource(this, R.drawable.ic_dialog_time))
                        .setIntent(zmanimIntent)
                        .build();

                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutMizrach, shortcutZmanim));
            }
//        }
    }

    private void viewFragment(String fragment) {
        if (fragment != null && fragment.equals("Kotel")) {
            mNavigationDrawerFragment.closeDrawer();
            currentPos = KOTEL_POSITION;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,
                            KotelFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        } else if (fragment != null && fragment.equals("Zmanim")) {
            mNavigationDrawerFragment.closeDrawer();
            currentPos = ZMANIM_POSITION;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container,
                            ZmanimFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppSettings.getInstance().updateLocalLanguage(this);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData(getString(R.string.menu_title), getString(R.string.menu_title_sub), BitmapFactory.decodeResource(getResources(), R.drawable.avatar));

        AppSettings.getInstance().checkToDisplayDateNotification();
        AppSettings.getInstance().setNextDayAlarm();
        AppSettings.getInstance().setNextTimeNotificaitons();
        setupLocationRequest();

        setupShortcuts();
        viewFragment(getIntent().getStringExtra("fragment"));

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        currentPos = position;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case SETTINGS_POSITION:
                fragmentManager
                        .beginTransaction().replace(R.id.container,
                        new PreferencesScreen())
                        .addToBackStack(null)
                        .commit();
                break;
            case ABOUT_POSITION:
                fragmentManager
                        .beginTransaction().replace(R.id.container,
                        new AboutFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case ZMANIM_POSITION:
                fragmentManager
                        .beginTransaction().replace(R.id.container,
                        new ZmanimFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case LOCATIONS_POSITION:
                fragmentManager
                        .beginTransaction().replace(R.id.container,
                        new LocationsFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case KOTEL_POSITION:
                fragmentManager
                        .beginTransaction().replace(R.id.container,
                        new KotelFragment())
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                super.onBackPressed();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationFind();
        stopIntentService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopIntentService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        startLocationFind();
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        startService(intent);
    }

    public void stopIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        stopService(intent);

    }

    public void locationUpdated() {
        if (currentPos == ZMANIM_POSITION) {
            onNavigationDrawerItemSelected(ZMANIM_POSITION);
        }
    }


    //    location methods
    private void setupLocationRequest() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
        // Note that location updates are off until the user turns them on
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (!firstRequest) {
            firstRequest = true;
            getLatestLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
        firstRequest = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location services changed.");
        updateLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.toString());
        firstRequest = false;
    }

    public void startLocationFind() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        } else {
            getLatestLocation();
        }
    }

    private void stopLocationFind() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    private void getLatestActualLocation() {
        updateLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        if (Geocoder.isPresent()) {
            startIntentService();
        } else {
            AppSettings.getInstance().removeLocationData();
        }

    }

    private void getLatestLocation() {
        if (!checkingPremissions) {
            checkingPremissions = true;
            PermissionManager.requestPermission(this, "explanation in case of deny", new PermissionManager.PermissionsListener() {
                @Override
                public void onDeny() {
                    Toast.makeText(MainActivity.this, "on LOCATION onDeny", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAllow() {
                    getLatestActualLocation();
                }
            }, PermissionManager.ACCESS_COARSE_LOCATION);
            checkingPremissions = false;
        }
    }

    private void requestLocationUpdates() {
        PermissionManager.requestPermission(this, "explanation in case of deny", new PermissionManager.PermissionsListener() {
            @Override
            public void onDeny() {
                Toast.makeText(MainActivity.this, "on LOCATION onDeny", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAllow() {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
                }
            }
        }, PermissionManager.ACCESS_COARSE_LOCATION);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionManager.onResult(requestCode, permissions, grantResults);
    }

    private void updateLocation(Location lastLocation) {
        if (lastLocation != null) {
            double currentLatitude = Utils.truncateExponent(lastLocation.getLatitude());
            double currentLongitude = Utils.truncateExponent(lastLocation.getLongitude());
            double currentAltitude = Utils.truncateExponent(lastLocation.getAltitude());

            boolean locationChanged = currentLatitude != AppSettings.getInstance().getLatitude() ||
                    currentLongitude != AppSettings.getInstance().getLongitude() || currentAltitude != AppSettings.getInstance().getAltitude();
            if (locationChanged) {
                AppSettings.getInstance().setLocation(currentLatitude, currentLongitude, currentAltitude);
                locationUpdated();
            }
        } else {
            requestLocationUpdates();
        }
    }


}
