package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.devspark.progressfragment.ProgressFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
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
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.CustomSwipeRefreshLayout;
import bd.com.ipay.ipayskeleton.CustomView.MakePaymentContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Fragments.IPaySupportPlaceAutocompleteFragment;
import bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments.TransactionHistoryCompletedFragment;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.CustomBusinessContact;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.IPayHereRequestUrlBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.IPayHereResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.NearbyBusinessResponseList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistoryResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import de.hdodenhof.circleimageview.CircleImageView;

public class IpayHereFragment extends ProgressFragment implements PlaceSelectionListener, HttpResponseListener {

    private static final int REQUEST_LOCATION = 1;
    public static final int LOCATION_SETTINGS_PERMISSION_CODE = 9876;

    private List<NearbyBusinessResponseList> mNearByBusinessResponse;
    private HttpRequestGetAsyncTask mIPayHereTask = null;

    private IPayHereResponse mIPayHereResponse;
    private LocationManager locationManager;
    private String mLatitude = "23.780879";
    private String mLongitude = "90.400956";

    private RecyclerView mTransactionHistoryRecyclerView;
    private BusinessContactListAdapter mTransactionHistoryAdapter;
    private LinearLayoutManager mLayoutManager;

    private ProgressDialog mProgressDialog;

    private CustomSwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyListTextView;

    private boolean isLoading = false;
    private boolean clearListAfterLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_ipay_here, container, false);
        if (getActivity() != null)
            getActivity().setTitle(R.string.ipay_here);

        IPaySupportPlaceAutocompleteFragment autocompleteFragment = new IPaySupportPlaceAutocompleteFragment();

        //SupportPlaceAutocompleteFragment autocompleteFragment = new SupportPlaceAutocompleteFragment();
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

        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.address_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = (CustomSwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);

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
        getLocation();
        //fetchNearByBusiness(mLatitude, mLongitude);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLocationPermission();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }

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

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    try {

                        IPayHereResponse iPayHereResponse = gson.fromJson(result.getJsonString(), IPayHereResponse.class);
                        loadTransactionHistory(iPayHereResponse.getNearbyBusinessResponseList());
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                mSwipeRefreshLayout.setRefreshing(false);
                mIPayHereTask = null;
                if (this.isAdded()) setContentShown(true);


//                try {
//                    mNearByBusinessResponse = new ArrayList<>();
//                    mIPayHereResponse = gson.fromJson(result.getJsonString(), IPayHereResponse.class);
//                    mNearByBusinessResponse = mIPayHereResponse.getNearbyBusinessResponseList();
//
//                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
//                        System.out.println("Test  Loc "+mNearByBusinessResponse.size());
//
//                        setBusinessContactAdapter(mNearByBusinessResponse);
//
//                        //readItems();
//                    } else {
//                        Toast.makeText(getContext(), mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Toast.makeText(getContext(), mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
//                }
//                mSwipeRefreshLayout.setRefreshing(false);
//                mIPayHereTask = null;
//                break;
        }

    }

    @Override
    public void onError(Status status) {
        Toast.makeText(getContext(), "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission) || Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission)) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            getLocationWithoutPermision();
                        } else {
                            getLocationPermission();
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_SETTINGS_PERMISSION_CODE) {
            getLocationPermission();
        }
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

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Utilities.isNecessaryPermissionExists(getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})) {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
            } else {
                getLocationsettings();
            }
        } else {
            getLocationsettings();
        }
    }

    private void getLocationsettings() {

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        Location location;
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            mLatitude = String.valueOf(latitude);
            mLongitude = String.valueOf(longitude);
            fetchNearByBusiness(this.mLatitude, this.mLongitude);
        } else {
            fetchNearByBusiness(this.mLatitude, this.mLongitude);
        }
    }

    private void getLocationWithoutPermision() {
        fetchNearByBusiness(this.mLatitude, this.mLongitude);
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


    protected void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Location Service Disabled")
                .setMessage("iPay needs to access your location to show iPay enabled outlets near you.")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), LOCATION_SETTINGS_PERMISSION_CODE);
                    }
                })
                .setNegativeButton("Continue Anyway", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        getLocationWithoutPermision();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
                        //Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lon);
                        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon);
//                        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?saddr="+mLatitude+","+mLongitude+"&daddr="+lat+","+lon);
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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do whatever you want on clicking the normal items


                    }
                });
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


}
