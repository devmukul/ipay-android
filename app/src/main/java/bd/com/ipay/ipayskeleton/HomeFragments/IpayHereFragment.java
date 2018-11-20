package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
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

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.MakePaymentContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.CustomBusinessContact;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.IPayHereRequestUrlBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.IPayHereResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.IPayHere.V2.NearbyBusinessResponseList;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import de.hdodenhof.circleimageview.CircleImageView;

public class IpayHereFragment extends BaseFragment implements PlaceSelectionListener, HttpResponseListener {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocationPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_ipay_here, container, false);
        if (getActivity() != null)
            getActivity().setTitle(R.string.ipay_here);

        SupportPlaceAutocompleteFragment autocompleteFragment = new SupportPlaceAutocompleteFragment();
        android.support.v4.app.FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_content, autocompleteFragment);
        ft.commit();
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
        .setTypeFilter(Place.TYPE_COUNTRY)
        .setCountry("BD")
        .build();
        // Register a listener to receive callbacks when a place has been selected or an error ha occurred.
        autocompleteFragment.setOnPlaceSelectedListener(this);
        autocompleteFragment.setFilter(autocompleteFilter);

        mTransactionHistoryRecyclerView = (RecyclerView) v.findViewById(R.id.address_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());
        mTransactionHistoryRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
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
                    mNearByBusinessResponse = new ArrayList<>();
                    mIPayHereResponse = gson.fromJson(result.getJsonString(), IPayHereResponse.class);
                    mNearByBusinessResponse = mIPayHereResponse.getNearbyBusinessResponseList();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        System.out.println("Test  Loc "+mNearByBusinessResponse.size());

                        setBusinessContactAdapter(mNearByBusinessResponse);

                        //readItems();
                    } else {
                        Toast.makeText(getContext(), mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), mIPayHereResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

                mIPayHereTask = null;
                break;
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
            this.mLatitude = String.valueOf(attributions.latitude);
            this.mLongitude = String.valueOf(attributions.longitude);
            fetchNearByBusiness(this.mLatitude, this.mLongitude);

        }
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                getLocationWithoutPermision();
            } else {
                getLocation();
            }
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


    private void setBusinessContactAdapter(List<NearbyBusinessResponseList> businessContactList) {
        mTransactionHistoryAdapter = new BusinessContactListAdapter(getContext(), businessContactList);
        mTransactionHistoryRecyclerView.setAdapter(mTransactionHistoryAdapter);
    }

    private class BusinessContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        Context c;
        List<NearbyBusinessResponseList> userTransactionHistories;
        List<NearbyBusinessResponseList> mFilteredOutlets;

        public BusinessContactListAdapter(Context c, List<NearbyBusinessResponseList> userTransactionHistories) {
            this.c = c;
            this.userTransactionHistories = userTransactionHistories;
            this.mFilteredOutlets = userTransactionHistories;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private TextView businessNameView;
            private TextView outletNameView;
            private TextView businessTypeView;
            private ProfileImageView profilePictureView;
            private TextView businessAddressView;
            private View directionView;



            public ViewHolder(final View itemView) {
                super(itemView);
                businessNameView = itemView.findViewById(R.id.business_name);
                outletNameView = itemView.findViewById(R.id.outlet_name);
                businessTypeView = itemView.findViewById(R.id.business_type);
                profilePictureView = itemView.findViewById(R.id.profile_picture);
                businessAddressView = itemView.findViewById(R.id.business_address);
                directionView = itemView.findViewById(R.id.direction);
            }

            public void bindView(final int pos) {

                final NearbyBusinessResponseList businessContact = mFilteredOutlets.get(pos);

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
                }else {
                    outletNameView.setVisibility(View.GONE);
                }

                if (businessAddress != null && !businessAddress.isEmpty()) {
                    businessAddressView.setText(businessAddress);
                    businessAddressView.setVisibility(View.VISIBLE);
                }else {
                    businessAddressView.setVisibility(View.GONE);
                }

                profilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePictureUrl, false);

                directionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lon);
                        Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                });
            }
        }


        // Now define the view holder for Normal list item
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
            if (mFilteredOutlets == null)
                return 0;
            else
                return mFilteredOutlets.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }



    }


}
