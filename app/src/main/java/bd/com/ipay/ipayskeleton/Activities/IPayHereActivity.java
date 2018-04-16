package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.Coordinate;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.IPayHereRequestUrlBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.IPayHereResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.NearbyBusinessResponseList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import de.hdodenhof.circleimageview.CircleImageView;

public class IPayHereActivity extends BaseActivity implements PlaceSelectionListener, OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, HttpResponseListener,
        GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener{

    private static final int REQUEST_LOCATION = 1;

    private List <NearbyBusinessResponseList> mNearByBusinessResponse;
    private HttpRequestGetAsyncTask mIPayHereTask = null;

    private SupportMapFragment mapFragment;
    private ProgressDialog mProgressDialog;
    private GoogleMap mMap;

    private IPayHereResponse mIPayHereResponse;
    private LocationManager locationManager;
    private String mLatitude;
    private String mLongitude;
    private boolean isStartedMoving = false;
    private CardView searchLocationView;
    private Button searchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipay_here);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Utilities.hideKeyboard(this);

        // Retrieve the PlaceAutocompleteFragment.
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("BD")
                .build();
        // Register a listener to receive callbacks when a place has been selected or an error ha occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(autocompleteFilter);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.please_wait));
        searchLocationView = (CardView) findViewById(R.id.search_this_place);
        searchLocation = (Button) findViewById(R.id.seach_this_place_btn);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getLocation();
        }

        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng initialLoc= mMap.getCameraPosition().target;
                searchLocationView.setVisibility(View.INVISIBLE);

                if (initialLoc != null) {
                    mMap.clear();
                    isStartedMoving = false;
                    mLatitude = String.valueOf(initialLoc.latitude);
                    mLongitude = String.valueOf(initialLoc.longitude);
                    fetchNearByBusiness(mLatitude, mLongitude);

                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public Context setContext() {
        return IPayHereActivity.this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if(Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)){
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            finish();
                        }else {
                            getLocation();
                        }
                    }
                }
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        readItems();

    }

    @Override
    public void onPlaceSelected(Place place) {
        LatLng attributions = place.getLatLng();
        if (attributions != null) {
            mMap.clear();
            this.mLatitude = String.valueOf(attributions.latitude);
            this.mLongitude = String.valueOf(attributions.longitude);
            fetchNearByBusiness(this.mLatitude, this.mLongitude);

        }
    }

    /**
     * Callback invoked when PlaceAutocompleteFragment encounters an error.
     */
    @Override
    public void onError(Status status) {
        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    private void getLocation() {
        if (!Utilities.isNecessaryPermissionExists(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION })) {
            ActivityCompat.requestPermissions(IPayHereActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, REQUEST_LOCATION);
        } else {
            Location location;
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            } else {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                mLatitude = String.valueOf(latitude);
                mLongitude = String.valueOf(longitude);
                fetchNearByBusiness(this.mLatitude, this.mLongitude);
            }else{
                finish();
                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void fetchNearByBusiness(String lattitude, String longitude) {
        if (mIPayHereTask != null)
            return;

        mProgressDialog.show();;
        String url = IPayHereRequestUrlBuilder.generateUri(lattitude, longitude);
        mIPayHereTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NEREBY_BUSSINESS,
                url, IPayHereActivity.this);
        mIPayHereTask.mHttpResponseListener = this;
        mIPayHereTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setUpMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void readItems() {
        startDemo();
        for (int i = 0; i < mNearByBusinessResponse.size(); i++) {

            NearbyBusinessResponseList iPayHereResponse = mNearByBusinessResponse.get(i);
            Coordinate cc = mNearByBusinessResponse.get(i).getCoordinate();
            Marker mMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(cc.getLatitude(), cc.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_ipay_here_marker))
                    .title(mNearByBusinessResponse.get(i).getBusinessName()));

            mMarker.setTag(iPayHereResponse);
        }
    }

    void startDemo() {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(mLatitude),Double.valueOf(mLongitude)), 13f ));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
    }

    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            hideProgressDialog();
            mIPayHereTask = null;
            Toast.makeText(IPayHereActivity.this, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new Gson();

        switch (result.getApiCommand()) {

            case Constants.COMMAND_GET_NEREBY_BUSSINESS:
                hideProgressDialog();

                try {
                    mNearByBusinessResponse = new ArrayList<>();
                    mIPayHereResponse = gson.fromJson(result.getJsonString(), IPayHereResponse.class);
                    mNearByBusinessResponse = mIPayHereResponse.getNearbyBusinessResponseList();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        setUpMap();
                    } else {
                        Toast.makeText(IPayHereActivity.this, mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(IPayHereActivity.this, mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

                mIPayHereTask = null;
                break;
        }

    }

    @Override
    public void onCameraIdle() {

        if(isStartedMoving)
            searchLocationView.setVisibility(View.VISIBLE);
        else
            searchLocationView.setVisibility(View.INVISIBLE);


    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            isStartedMoving = true ;
        }
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private View view;
        private Marker marker;
        boolean not_first_time_showing_info_window = false;
        private CircleImageView businessProfileImageView;
        private TextView businessNameTextView;
        public CustomInfoWindowAdapter() {
            view = IPayHereActivity.this.getLayoutInflater().inflate(R.layout.ipay_here_info_window_map,
                    null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (this.marker != null
                    && this.marker.isInfoWindowShown()) {
                this.marker.hideInfoWindow();
                this.marker.showInfoWindow();
            }
            return null;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            this.marker = marker;
            NearbyBusinessResponseList infoWindowData = (NearbyBusinessResponseList) marker.getTag();
            businessProfileImageView = (CircleImageView) view.findViewById(R.id.profile_picture);
            businessNameTextView = (TextView) view.findViewById(R.id.textview_name);
            String title = infoWindowData.getBusinessName();
            businessNameTextView.setText(title);
            if (infoWindowData.getImageUrl() != null ) {
                String imageUrl = Constants.BASE_URL_FTP_SERVER + infoWindowData.getImageUrl();
                if (not_first_time_showing_info_window) {
                    not_first_time_showing_info_window = false;
                    Glide.with(IPayHereActivity.this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_business_logo_round)
                            .error(R.drawable.ic_business_logo_round)
                            .into(businessProfileImageView);
                } else {
                    not_first_time_showing_info_window = true;
                    Glide.with(IPayHereActivity.this).load(imageUrl)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    return false;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    marker.showInfoWindow();
                                    return false;
                                }
                            }).crossFade().placeholder(R.drawable.ic_business_logo_round)
                            .error(R.drawable.ic_business_logo_round).into(businessProfileImageView);;

                }
            }
            return view;
        }
    }

    private void hideProgressDialog() {
         mProgressDialog.dismiss();
    }
}
