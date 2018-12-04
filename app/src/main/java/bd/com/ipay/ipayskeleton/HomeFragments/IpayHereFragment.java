package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Fragments.IPaySupportPlaceAutocompleteFragment;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.IPayHereRequestUrlBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.IPayHereResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.NearbyBusinessResponseList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IpayHereFragment extends ProgressFragment implements PlaceSelectionListener, HttpResponseListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = IpayHereFragment.class.getSimpleName();

    private static final int REQUEST_LOCATION = 1;
    public static final int LOCATION_SETTINGS_PERMISSION_CODE = 9876;

    private List<NearbyBusinessResponseList> mNearByBusinessResponse;
    private HttpRequestGetAsyncTask mIPayHereTask = null;
    private LocationManager locationManager;
    private String mLatitude = "23.706325";
    private String mLongitude = "90.316801";

    private RecyclerView mTransactionHistoryRecyclerView;
    private BusinessContactListAdapter mTransactionHistoryAdapter;
    private LinearLayoutManager mLayoutManager;

    private ProgressDialog mProgressDialog;

    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private boolean isLoading = false;
    private boolean clearListAfterLoading;

    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 5000; // = 5 seconds
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;

    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.
                        toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        googleApiClient = new GoogleApiClient.Builder(getContext()).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(getActivity()).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }

                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            mLatitude = String.valueOf(location.getLatitude());
            mLongitude = String.valueOf(location.getLongitude());
        }

        startLocationUpdates();
        fetchNearByBusiness(mLatitude, mLongitude);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_ipay_here, container, false);
        if (getActivity() != null)
            getActivity().setTitle(R.string.ipay_here);

        IPaySupportPlaceAutocompleteFragment autocompleteFragment = new IPaySupportPlaceAutocompleteFragment();
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_content, autocompleteFragment);
        ft.commit();

        autocompleteFragment.setOnSearchClearListener(new IPaySupportPlaceAutocompleteFragment.OnSearchClearListener() {
            @Override
            public void onClear() {
                mProgressDialog.show();
                refreshTransactionHistory();
            }
        });


        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry("BD")
                .build();
        // Register a listener to receive callbacks when a place has been selected or an error ha occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(autocompleteFilter);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);

        mTransactionHistoryRecyclerView = v.findViewById(R.id.address_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        mEmptyListTextView = v.findViewById(R.id.empty_list_text);

        mSwipeRefreshLayout.setOnRefreshListener(new CustomSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity()) && mIPayHereTask == null) {
                    refreshTransactionHistory();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        setupRecyclerView();
        return v;
    }

    private void refreshTransactionHistory() {
        clearListAfterLoading = true;
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, getContext())) {
            mIPayHereTask = null;
            return;
        }
        Gson gson = new Gson();

        switch (result.getApiCommand()) {

            case Constants.COMMAND_GET_NEREBY_BUSSINESS:

                    try {
                        IPayHereResponse iPayHereResponse = gson.fromJson(result.getJsonString(), IPayHereResponse.class);
                        if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                            loadTransactionHistory(iPayHereResponse.getNearbyBusinessResponseList());
                        } else {
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), iPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), "Could not fetch data..", Toast.LENGTH_LONG).show();
                    }

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                mSwipeRefreshLayout.setRefreshing(false);
                mIPayHereTask = null;
                if (this.isAdded()) setContentShown(true);
        }

    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getContext(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaceSelected(Place place) {
        LatLng attributions = place.getLatLng();
        if (attributions != null) {
            mProgressDialog.show();
            this.mLatitude = String.valueOf(attributions.latitude);
            this.mLongitude = String.valueOf(attributions.longitude);
            clearListAfterLoading = true;
            fetchNearByBusiness(this.mLatitude, this.mLongitude);

        }
    }

    private void fetchNearByBusiness(String lattitude, String longitude) {
        if (mIPayHereTask != null)
            return;

        String url = IPayHereRequestUrlBuilder.generateUri(lattitude, longitude);
        mIPayHereTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_NEREBY_BUSSINESS,
                url, getContext(), false);
        mIPayHereTask.mHttpResponseListener = this;
        mIPayHereTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setupRecyclerView() {
        mTransactionHistoryAdapter = new BusinessContactListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
    }

    private void loadTransactionHistory(List<NearbyBusinessResponseList> transactionHistories) {
        if (clearListAfterLoading || mNearByBusinessResponse == null || mNearByBusinessResponse.size() == 0) {
            mNearByBusinessResponse = transactionHistories;
            clearListAfterLoading = false;
        } else {
            List<NearbyBusinessResponseList> tempTransactionHistories;
            tempTransactionHistories = transactionHistories;
            mNearByBusinessResponse.addAll(tempTransactionHistories);
        }
        if (mNearByBusinessResponse != null && mNearByBusinessResponse.size() > 0)
            mEmptyListTextView.setVisibility(View.GONE);
        else
            mEmptyListTextView.setVisibility(View.VISIBLE);

        if (isLoading)
            isLoading = false;
        mTransactionHistoryAdapter.notifyDataSetChanged();
        setContentShown(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    private class BusinessContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int FOOTER_VIEW = 1;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView businessNameView;
            private TextView outletNameView;
            private TextView distanceView;
            private ProfileImageView profilePictureView;
            private TextView businessAddressView;
            private View directionView;

            public ViewHolder(final View itemView) {
                super(itemView);
                businessNameView = itemView.findViewById(R.id.business_name);
                outletNameView = itemView.findViewById(R.id.outlet_name);
                distanceView = itemView.findViewById(R.id.distance);
                profilePictureView = itemView.findViewById(R.id.profile_picture);
                businessAddressView = itemView.findViewById(R.id.business_address);
                directionView = itemView.findViewById(R.id.direction);
            }

            public void bindView(int pos) {
                final NearbyBusinessResponseList businessContact = mNearByBusinessResponse.get(pos);

                //final String typeInList = businessContact.getTypeInList();
                final String businessName = businessContact.getBusinessName();
                final String mobileNumber = businessContact.getMobileNumber();
                //final String businessType = businessContact.getBusinessType();
                final String profilePictureUrl = businessContact.getImageUrl();
                final String businessAddress = businessContact.getAddressString();
                final String businessOutlet = businessContact.getOutletName();
                final Long businessOutletId = businessContact.getOutletId();

                final double lat = businessContact.getCoordinate().getLatitude();
                final double lon = businessContact.getCoordinate().getLongitude();

                if (businessName != null && !businessName.isEmpty())
                    businessNameView.setText(businessName);

                if (businessOutlet != null && !businessOutlet.isEmpty()) {
                    outletNameView.setText(businessOutlet);
                    outletNameView.setVisibility(View.VISIBLE);
                } else {
                    outletNameView.setVisibility(View.GONE);
                }

                if (businessAddress != null && !businessAddress.isEmpty()) {
                    businessAddressView.setText(businessAddress);
                    businessAddressView.setVisibility(View.VISIBLE);
                } else {
                    businessAddressView.setVisibility(View.GONE);
                }

                float[] result = new float[1];
                Location.distanceBetween(Double.parseDouble(mLatitude), Double.parseDouble(mLongitude), lat, lon, result);
                distanceView.setText("Distance : " + distanceText(result[0]));

                profilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePictureUrl, false);

                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!mSwipeRefreshLayout.isRefreshing()) {
                            Intent intent = new Intent(getActivity(), IPayTransactionActionActivity.class);
                            intent.putExtra(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_MAKE_PAYMENT);
                            intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
                            intent.putExtra(Constants.FROM_QR_SCAN, true);
                            intent.putExtra(Constants.NAME, businessName);
                            intent.putExtra(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + profilePictureUrl);
                            intent.putExtra(Constants.ADDRESS, businessAddress);
                            if (businessOutletId != null) {
                                intent.putExtra(Constants.OUTLET_ID, businessOutletId.longValue());
                            }
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        public String distanceText(float distance) {
            String distanceString;

            if (distance < 1000)
                if (distance < 1)
                    distanceString = String.format(Locale.US, "%dm", 1);
                else
                    distanceString = String.format(Locale.US, "%dm", Math.round(distance));
            else if (distance > 10000)
                distanceString = String.format(Locale.US, "%dkm", Math.round(distance / 1000));
            else
                distanceString = String.format(Locale.US, "%.2fkm", distance / 1000);

            return distanceString;
        }

        class NormalViewHolder extends ViewHolder {
            NormalViewHolder(final View itemView) {
                super(itemView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ipay_here, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                NormalViewHolder vh = (NormalViewHolder) holder;
                vh.bindView(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            // Return +1 as there's an extra footer (Load more...)
            if (mNearByBusinessResponse != null && !mNearByBusinessResponse.isEmpty())
                return mNearByBusinessResponse.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }
}
