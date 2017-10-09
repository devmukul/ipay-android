package bd.com.ipay.ipayskeleton.DataCollectors.Service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DataCollectors.Model.LocationCollector;
import bd.com.ipay.ipayskeleton.DataCollectors.Model.UserLocation;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LocationCollectorService extends Service implements HttpResponseListener, LocationListener {

    public static final String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    /**
     * Constant for getting min location update time.
     */
    private static final long LOCATION_UPDATE_MIN_TIME_INTERVAL = 2 * 60 * 1000;
    /**
     * Constant for getting min distance change.
     */
    private static final long LOCATION_UPDATE_MIN_DISTANCE = 500;

    /**
     * The network may be not available always. This broadcast will receive the network connection notification as
     * this receiver will be registered with {@link ConnectivityManager.CONNECTIVITY_ACTION} action.
     */
    private final BroadcastReceiver networkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (Utilities.isConnectionAvailable(LocationCollectorService.this)) {
                    if (locationUpdateRequestAsyncTask == null) {
                        if (dataHelper == null) {
                            dataHelper = DataHelper.getInstance(LocationCollectorService.this);
                        }
                        userLocationList = dataHelper.getAllSavedLocation();
                        sendUserLocation();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private DataHelper dataHelper;
    private List<UserLocation> userLocationList;
    private HttpRequestPostAsyncTask locationUpdateRequestAsyncTask;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = DataHelper.getInstance(this);
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Utilities.isNecessaryPermissionExists(this, LOCATION_PERMISSIONS) && locationManager != null) {
            final String locationProvider;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
            } else {
                locationProvider = LocationManager.NETWORK_PROVIDER;
            }
            locationManager.requestLocationUpdates(locationProvider, LOCATION_UPDATE_MIN_TIME_INTERVAL, LOCATION_UPDATE_MIN_DISTANCE, this);
        }
        registerReceiver(networkBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkBroadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result != null) {
            switch (result.getStatus()) {
                case Constants.HTTP_RESPONSE_STATUS_OK:
                    if (userLocationList != null && dataHelper != null) {
                        dataHelper.deleteLocations(userLocationList);
                        locationUpdateRequestAsyncTask = null;
                    }
                    break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            UserLocation userLocation = new UserLocation(location.getLatitude(), location.getLongitude());
            if (Utilities.isConnectionAvailable(this)) {
                if (locationUpdateRequestAsyncTask == null) {
                    try {
                        // Before sending the users current location we will fetch any previously saved location on our
                        // SQLite database and add the current location with the list. So if there isn't any data from
                        // database we will add one userLocation to out empty list.
                        userLocationList = dataHelper.getAllSavedLocation();
                        userLocationList.add(userLocation);
                        sendUserLocation();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    dataHelper.saveLocation(userLocation);
                }
            } else {
                dataHelper.saveLocation(userLocation);
            }
        }
    }

    private void sendUserLocation() {
        LocationCollector locationCollector = new LocationCollector();
        locationCollector.setDeviceId(DeviceInfoFactory.getDeviceId(this));
        locationCollector.setUuid(ProfileInfoCacheManager.getUUID());
        locationCollector.setMobileNumber(ProfileInfoCacheManager.getMobileNumber());
        locationCollector.setLocationList(userLocationList);
        String body = new GsonBuilder().create().toJson(locationCollector);
        locationUpdateRequestAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_POST_USER_LOCATION, Constants.BASE_URL_DATA_COLLECTOR + Constants.URL_ENDPOINT_LOCATION_COLLECTOR, body, this, this);
        locationUpdateRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
