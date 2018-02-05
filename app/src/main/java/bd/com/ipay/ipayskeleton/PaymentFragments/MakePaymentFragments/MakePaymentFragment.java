package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.BusinessContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAllBusinessListAsyncTask;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.BusinessContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserAddress;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MakePaymentFragment extends BaseFragment implements LocationListener, HttpResponseListener {


    private static final int REQUEST_CODE_PERMISSION = 1001;

    private final int PICK_CONTACT_REQUEST = 100;
    private final int PAYMENT_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressDialog mProgressDialog;

    private Button buttonPayment;
    private ImageView buttonSelectFromContacts;
    private ImageView buttonScanQRCode;
    private BusinessContactsSearchView mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private EditText mRefNumberEditText;
    private TextView mBalanceView;
    private View profileView;
    private View mobileNumberView;

    private ProgressDialog mProgressDialog;

    private ProfileImageView businessProfileImageView;
    private TextView businessNameTextView;
    private TextView businessMobileNumberTextView;
    private TextView mAddressTextView;
    private TextView mAddressCountryAndDistrictTextView;


    private String mReceiverMobileNumber;
    private String mReceiverName;
    private String mReceiverPhotoUri;
    private String mAddressString;
    private String mDistrict;
    private String mCountry;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_make_payment, container, false);
        getActivity().setTitle(R.string.make_payment);

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);

        mMobileNumberEditText = (BusinessContactsSearchView) v.findViewById(R.id.mobile_number);
        profileView = (LinearLayout) v.findViewById(R.id.profile);
        mobileNumberView = (RelativeLayout) v.findViewById(R.id.mobile_number_view);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mRefNumberEditText = (EditText) v.findViewById(R.id.reference_number);
        mAddressTextView = (TextView) v.findViewById(R.id.textview_address_line_1);
        mAddressCountryAndDistrictTextView = (TextView) v.findViewById(R.id.textview_address_line_2);

        businessProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        businessNameTextView = (TextView) v.findViewById(R.id.textview_name);
        businessMobileNumberTextView = (TextView) v.findViewById(R.id.textview_mobile_number);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        buttonScanQRCode = (ImageView) v.findViewById(R.id.button_scan_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        buttonPayment = (Button) v.findViewById(R.id.button_payment);

        mProgressDialog = new ProgressDialog(getContext());
        mBalanceView = (TextView) v.findViewById(R.id.balance_view);

        mBalanceView.setText(SharedPrefManager.getUserBalance());

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mobileNumberView.setVisibility(View.GONE);
            profileView.setVisibility(View.VISIBLE);
            mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);
            businessMobileNumberTextView.setText(mReceiverMobileNumber);
            if (getActivity().getIntent().hasExtra(Constants.NAME)) {
                mReceiverName = getActivity().getIntent().getStringExtra(Constants.NAME);
                mReceiverPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                if (!TextUtils.isEmpty(mReceiverPhotoUri)) {
                    businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                }
                if (TextUtils.isEmpty(mReceiverName)) {
                    businessNameTextView.setVisibility(View.GONE);
                } else {
                    businessNameTextView.setVisibility(View.VISIBLE);
                    businessNameTextView.setText(mReceiverName);
                }
                if (getActivity().getIntent().getStringExtra(Constants.ADDRESS) != null &&
                        getActivity().getIntent().getStringExtra(Constants.COUNTRY) != null &&
                        getActivity().getIntent().getStringExtra(Constants.DISTRICT) != null) {
                    mAddressString = getActivity().getIntent().getStringExtra(Constants.ADDRESS);
                    mCountry = getActivity().getIntent().getStringExtra(Constants.COUNTRY);
                    mDistrict = getActivity().getIntent().getStringExtra(Constants.DISTRICT);
                    mAddressTextView.setVisibility(View.VISIBLE);
                    mAddressCountryAndDistrictTextView.setVisibility(View.VISIBLE);
                    mAddressTextView.setText(mAddressString);
                    mAddressCountryAndDistrictTextView.setText(mDistrict + " , " + mCountry);
                } else if (getArguments() != null) {
                    try {
                        mAddressString = getArguments().getString(Constants.ADDRESS);
                        mCountry = getArguments().getString(Constants.COUNTRY);
                        mDistrict = getArguments().getString(Constants.DISTRICT);
                        mAddressTextView.setText(mAddressString);
                        mAddressCountryAndDistrictTextView.setText(mDistrict + " , " + mCountry);
                        mAddressTextView.setVisibility(View.VISIBLE);
                        mAddressCountryAndDistrictTextView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        getProfileInfo(mReceiverMobileNumber);
                    }

                } else {
                    getProfileInfo(mReceiverMobileNumber);
                }
            } else {
                getProfileInfo(mReceiverMobileNumber);
            }
        } else {
            profileView.setVisibility(View.GONE);
            mobileNumberView.setVisibility(View.VISIBLE);
        }

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BusinessContactPickerDialogActivity.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        buttonPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    Utilities.hideKeyboard(getContext(), getView());
                    if (verifyUserInputs()) {
                        if (PaymentActivity.mMandatoryBusinessRules.IS_LOCATION_REQUIRED()) {
                            if (Utilities.hasForcedLocationPermission(MakePaymentFragment.this)) {
                                getLocationAndLaunchReviewPage();
                            }
                        } else {
                            launchReviewPage(null);
                        }
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        buttonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.performQRCodeScan(MakePaymentFragment.this, REQUEST_CODE_PERMISSION);

            }
        });

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_MAKE_PAYMENT);
        // Start a syncing for business account list
        syncBusinessAccountList();

        return v;
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndLaunchReviewPage() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.show();
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
        } else {
            Utilities.showGPSHighAccuracyDialog(this);
        }
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getContext(), this);
        mProgressDialog.setMessage(getActivity().getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_make_payment));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.initiateQRCodeScan(this);
                } else {
                    Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            case Utilities.LOCATION_SETTINGS_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.initiateQRCodeScan(this);
                } else {
                    buttonPayment.performClick();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            if (mobileNumber != null) {
                mMobileNumberEditText.setText(mobileNumber);
            }
        } else if (requestCode == PAYMENT_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        } else if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (InputValidator.isValidNumber(result)) {
                            mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(result));
                        } else if (getActivity() != null)
                            Toast.makeText(getActivity(), getResources().getString(
                                    R.string.scan_valid_ipay_qr_code), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if (requestCode == Utilities.LOCATION_SETTINGS_RESULT_CODE || requestCode == Utilities.LOCATION_SOURCE_SETTINGS_RESULT_CODE) {
            buttonPayment.performClick();
        }
    }

    private void syncBusinessAccountList() {
        int lastBusinessId = DataHelper.getInstance(getActivity()).getLastAddedBusinessId();
        GetAllBusinessContactRequestBuilder mGetAllBusinessContactRequestBuilder = new GetAllBusinessContactRequestBuilder(lastBusinessId);
        new GetAllBusinessListAsyncTask(getActivity(), mGetAllBusinessContactRequestBuilder.getGeneratedUri()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage = null;

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            //validation check of amount
            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                focusView = mAmountEditText;
                mAmountEditText.setError(getString(R.string.please_enter_amount));
                cancel = true;

            } else {
                final BigDecimal paymentAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (paymentAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                }
                if (Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

                    final BigDecimal minimumPaymentAmount = PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumPaymentAmount = PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), paymentAmount, minimumPaymentAmount, maximumPaymentAmount);
                }
            }
        } else {
            focusView = mAmountEditText;
            errorMessage = getString(R.string.balance_not_available);
            cancel = true;
        }

        if (errorMessage != null) {
            focusView = mAmountEditText;
            mAmountEditText.setError(errorMessage);
            cancel = true;
        }
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        if (!(mDescriptionEditText.getText().toString().trim().length() > 0)) {
            focusView = mDescriptionEditText;
            mDescriptionEditText.setError(getString(R.string.please_write_note));
            cancel = true;
        }

        if (TextUtils.isEmpty(mReceiverMobileNumber)) {
            if (!InputValidator.isValidNumber(mobileNumber)) {
                focusView = mMobileNumberEditText;
                mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
                cancel = true;
            } else if (ContactEngine.formatMobileNumberBD(mobileNumber).equals(ProfileInfoCacheManager.getMobileNumber())) {
                focusView = mMobileNumberEditText;
                mMobileNumberEditText.setError(getString(R.string.you_cannot_make_payment_to_your_number));
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void launchReviewPage(@Nullable Location location) {

        mProgressDialog.dismiss();
        getActivity().getIntent().setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        String receiver;

        if (TextUtils.isEmpty(mReceiverMobileNumber)) {
            receiver = mMobileNumberEditText.getText().toString().trim();
        } else {
            receiver = mReceiverMobileNumber;
        }
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String referenceNumber = mRefNumberEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        Intent intent = new Intent(getActivity(), PaymentReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(receiver));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);
        intent.putExtra(Constants.REFERENCE_NUMBER, referenceNumber);
        intent.putExtra(Constants.ADDRESS, mAddressString);
        intent.putExtra(Constants.COUNTRY, mCountry);
        intent.putExtra(Constants.DISTRICT, mDistrict);
        if (location != null) {
            intent.putExtra(Constants.LATITUDE, location.getLatitude());
            intent.putExtra(Constants.LONGITUDE, location.getLongitude());
        }

        if (!TextUtils.isEmpty(mReceiverName)) {
            intent.putExtra(Constants.NAME, mReceiverName);
            intent.putExtra(Constants.PHOTO_URI, mReceiverPhotoUri);
        }
        startActivityForResult(intent, PAYMENT_REVIEW_REQUEST);
    }

    private void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null)
            return;
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();
        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onLocationChanged(Location location) {
        launchReviewPage(location);
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

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetBusinessRuleTask = null;
            mGetProfileInfoTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
            mProgressDialog.dismiss();
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {
                        for (BusinessRule rule : businessRuleArray) {
                            switch (rule.getRuleID()) {
                                case Constants.SERVICE_RULE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT:
                                    PaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                    break;
                                case Constants.SERVICE_RULE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT:
                                    PaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                    break;
                                case Constants.SERVICE_RULE_IS_LOCATION_REQUIRED:
                                    PaymentActivity.mMandatoryBusinessRules.setIS_LOCATION_REQUIRED(rule.getRuleValue().intValue() >= Constants.LOCATION_REQUIRED_TRUE);
                                    break;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
                }
            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
            }

            mGetBusinessRuleTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            Gson gson = new Gson();
            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String name = mGetUserInfoResponse.getName();
                    if (mGetUserInfoResponse.getAddressList().getOFFICE() != null) {
                        List<UserAddress> office = mGetUserInfoResponse.getAddressList().getOFFICE();
                        if (office != null) {
                            mAddressString = office.get(0).getAddressLine1();
                            mDistrict = office.get(0).getDistrict();
                            mCountry = office.get(0).getCountry();
                            mAddressTextView.setText(mAddressString);
                            mAddressCountryAndDistrictTextView.setText(mDistrict + " , " + mCountry);
                            mAddressTextView.setVisibility(View.VISIBLE);
                            mAddressCountryAndDistrictTextView.setVisibility(View.VISIBLE);
                        }
                    }

                    int accountType = mGetUserInfoResponse.getAccountType();

                    if (accountType != Constants.BUSINESS_ACCOUNT_TYPE) {
                        new AlertDialog.Builder(getContext())
                                .setMessage(R.string.not_a_business_user)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }

                    String profilePicture = null;
                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                    }


                    if (!TextUtils.isEmpty(profilePicture)) {
                        businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePicture, false);
                    }
                    if (TextUtils.isEmpty(name)) {
                        businessNameTextView.setVisibility(View.GONE);
                    } else {
                        businessNameTextView.setVisibility(View.VISIBLE);
                        businessNameTextView.setText(name);
                    }

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    Toaster.makeText(getContext(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                    getActivity().finish();
                } else {
                    Toaster.makeText(getContext(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                    getActivity().finish();
                }


            } catch (Exception e) {
                e.printStackTrace();

                Toaster.makeText(getContext(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                getActivity().finish();
            }

            mGetProfileInfoTask = null;
        }
    }
}
