package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.BusinessContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAllBusinessListAsyncTask;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.BusinessContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserAddress;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static android.view.View.GONE;

public class MakePaymentFragment extends BaseFragment implements LocationListener, HttpResponseListener {


    private static final int REQUEST_CODE_PERMISSION = 1001;

    private final int PICK_CONTACT_REQUEST = 100;
    private final int PAYMENT_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private HttpRequestPostAsyncTask mPaymentTask = null;
    private PaymentRequest mPaymentRequest;

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
    private ProgressBar mAddressProgressBar;

    private CustomProgressDialog mCustomProgressDialog;


    private ProfileImageView businessProfileImageView;
    private TextView businessNameTextView;
    private TextView businessMobileNumberTextView;
    private TextView mAddressTextView;
    private TextView mThanaAndDistrictTextView;
    private View mIconEditMobileNumber;


    private String mReceiverMobileNumber;
    private String mReceiverName;
    private String mReceiverPhotoUri;
    private String mAddressString;
    private String mDistrict;
    //private String mCountry;
    private String mThana;
    private String mAmount;
    private String mReceiver;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private Context mContext;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private LocationManager locationManager;

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

        mContext = getContext();

        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setCancelable(false);

        mCustomProgressDialog = new CustomProgressDialog((mContext));

        mMobileNumberEditText = (BusinessContactsSearchView) v.findViewById(R.id.mobile_number);
        mAddressProgressBar = (ProgressBar) v.findViewById(R.id.address_progress_bar);
        profileView = v.findViewById(R.id.profile);
        mobileNumberView = v.findViewById(R.id.mobile_number_holder);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mRefNumberEditText = (EditText) v.findViewById(R.id.reference_number);
        mAddressTextView = (TextView) v.findViewById(R.id.textview_address_line_1);
        mThanaAndDistrictTextView = (TextView) v.findViewById(R.id.textview_address_line_2);
        mProgressDialog = new ProgressDialog(getActivity());

        businessProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        businessProfileImageView.setBusinessLogoPlaceHolder();
        businessNameTextView = (TextView) v.findViewById(R.id.textview_name);
        businessMobileNumberTextView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mIconEditMobileNumber = v.findViewById(R.id.edit_icon_mobile_number);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        buttonScanQRCode = (ImageView) v.findViewById(R.id.button_scan_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        buttonPayment = (Button) v.findViewById(R.id.button_payment);

        mBalanceView = (TextView) v.findViewById(R.id.balance_view);

        mBalanceView.setText(SharedPrefManager.getUserBalance());

        PaymentActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.MAKE_PAYMENT);

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mobileNumberView.setVisibility(GONE);
            profileView.setVisibility(View.VISIBLE);
            if (getActivity().getIntent().hasExtra(Constants.PHOTO_URI)) {
                mReceiverPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                businessProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
            }
            mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);
            mMobileNumberEditText.setText(mReceiverMobileNumber);
            if (getActivity().getIntent().hasExtra(Constants.NAME)) {
                mReceiverName = getActivity().getIntent().getStringExtra(Constants.NAME);
                if (TextUtils.isEmpty(mReceiverName)) {
                    businessNameTextView.setVisibility(GONE);
                } else {
                    businessNameTextView.setVisibility(View.VISIBLE);
                    businessNameTextView.setText(mReceiverName);
                }
                if (getActivity().getIntent().getStringExtra(Constants.ADDRESS) != null &&
                        getActivity().getIntent().getStringExtra(Constants.DISTRICT) != null &&
                        getActivity().getIntent().getStringExtra(Constants.THANA) != null) {
                    mAddressString = getActivity().getIntent().getStringExtra(Constants.ADDRESS);
                    //mCountry = Utilities.getFormattedCountryName(getActivity().getIntent().getStringExtra(Constants.COUNTRY));
                    mDistrict = getActivity().getIntent().getStringExtra(Constants.DISTRICT);
                    mThana = getActivity().getIntent().getStringExtra(Constants.THANA);
                    mReceiverPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                    mAddressTextView.setVisibility(View.VISIBLE);
                    mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
                    mAddressTextView.setText(mAddressString);
                    mThanaAndDistrictTextView.setText(mThana + ", " + mDistrict);
                    if (mReceiverPhotoUri != null) {
                        businessProfileImageView.setBusinessProfilePicture
                                (Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                    }
                } else if (getArguments() != null) {
                    try {
                        mAddressString = getArguments().getString(Constants.ADDRESS);
                        //mCountry = Utilities.getFormattedCountryName(getArguments().getString(Constants.COUNTRY));
                        mDistrict = getArguments().getString(Constants.DISTRICT);
                        mThana = getArguments().getString(Constants.THANA);

                        if (mAddressString != null) {
                            mAddressTextView.setText(mAddressString);
                            mThanaAndDistrictTextView.setText(mThana + " , " + mDistrict);
                            mAddressTextView.setVisibility(View.VISIBLE);
                            mThanaAndDistrictTextView.setVisibility(View.VISIBLE);

                        }
                        if (getArguments().getString(Constants.PHOTO_URI) != null) {
                            mReceiverPhotoUri = getArguments().getString(Constants.PHOTO_URI);
                            businessProfileImageView.setBusinessProfilePicture
                                    (Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                        }
                    } catch (Exception e) {
                        getProfileInfo(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber), false);
                    }

                } else {
                    getProfileInfo(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber), false);
                }
            } else {
                getProfileInfo(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber), false);
            }
        } else {
            profileView.setVisibility(GONE);
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
                            attemptPaymentWithPinCheck();
                        }
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        buttonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);

            }
        });

        mIconEditMobileNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumberView.setVisibility(View.VISIBLE);
                profileView.setVisibility(GONE);
                mMobileNumberEditText.requestFocus();
            }
        });

        mMobileNumberEditText.setCustomTextChangeListener(new BusinessContactsSearchView.CustomTextChangeListener() {
            @Override
            public void onTextChange(String inputText) {

                if (profileView.getVisibility() == GONE
                        && Utilities.isConnectionAvailable(getActivity())
                        && InputValidator.isValidNumber(inputText)) {
                    Cursor mCursor = searchContact(inputText);
                    try {
                        if (mCursor != null) {
                            System.out.println(">> "+mCursor.getCount());
                            setValueFromCursor(mCursor);
                        }else {
                            getProfileInfo(ContactEngine.formatMobileNumberBD(inputText));
                            mReceiverMobileNumber = ContactEngine.formatMobileNumberBD(inputText);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        getProfileInfo(ContactEngine.formatMobileNumberBD(inputText));
                        mReceiverMobileNumber = ContactEngine.formatMobileNumberBD(inputText);
                    } finally {
                        if (mCursor != null) {
                            mCursor.close();
                        }
                    }


                }
            }

            @Override
            public void onTextChange(String inputText, String name, String imageURL, String address, String thanaDistrict) {

                if (imageURL != null && !imageURL.isEmpty()) {
                    mReceiverPhotoUri = imageURL;
                    businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                }
                if (!name.isEmpty()) {
                    mReceiverName = name;
                    businessNameTextView.setText(mReceiverName);
                }
                profileView.setVisibility(View.VISIBLE);
                mobileNumberView.setVisibility(GONE);
                mAddressTextView.setVisibility(View.VISIBLE);
                mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
                mThanaAndDistrictTextView.setText(thanaDistrict);
                mAddressTextView.setText(address);
                mReceiverMobileNumber = ContactEngine.formatMobileNumberBD(inputText);
                //getProfileInfo(ContactEngine.formatMobileNumberBD(inputText), false);

                mMobileNumberEditText.clearSelectedData();
            }
        });


        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_MAKE_PAYMENT);
        // Start a syncing for business account list
        syncBusinessAccountList();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_make_payment));
    }

    @Override
    public void onPause() {
        super.onPause();
        mCustomProgressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCustomProgressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                    startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                } else {
                    Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
            case Utilities.LOCATION_SETTINGS_PERMISSION_CODE: {
                buttonPayment.performClick();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    final String result = barcode.displayValue;
                    if (result != null) {
                        Handler mHandler = new Handler();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (InputValidator.isValidNumber(result)) {
                                    Cursor mCursor = searchContact(result);
                                    try {
                                        if (mCursor != null) {
                                            setValueFromCursor(mCursor);
                                        }else {
                                            mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(result));
                                            getProfileInfo(ContactEngine.formatMobileNumberBD(result));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(result));
                                        getProfileInfo(ContactEngine.formatMobileNumberBD(result));
                                    } finally {
                                        if (mCursor != null) {
                                            mCursor.close();
                                        }
                                    }
                                } else if (getActivity() != null)
                                    Toast.makeText(getActivity(), getResources().getString(
                                            R.string.scan_valid_ipay_qr_code), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    getActivity().finish();
                }
            } else {
                getActivity().finish();
            }
        } else if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            mMobileNumberEditText.setText("");
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            String name = data.getStringExtra(Constants.BUSINESS_NAME);
            String imageURL = data.getStringExtra(Constants.PROFILE_PICTURE);
            String address = data.getStringExtra(Constants.ADDRESS);
            String thanaDistrict = data.getStringExtra(Constants.THANA) +", "+data.getStringExtra(Constants.DISTRICT);
            //getProfileInfo(ContactEngine.formatMobileNumberBD(mobileNumber), false);
            mobileNumberView.setVisibility(View.GONE);
            profileView.setVisibility(View.VISIBLE);
            mAddressTextView.setVisibility(View.VISIBLE);
            mThanaAndDistrictTextView.setVisibility(View.VISIBLE);

            if (imageURL != null && !imageURL.isEmpty()) {
                mReceiverPhotoUri = imageURL;
                businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
            }
            if (!name.isEmpty()) {
                mReceiverName = name;
                businessNameTextView.setText(mReceiverName);
            }
            if (imageURL != null && !address.isEmpty()) {
                mAddressString = address;
                mAddressTextView.setText(mAddressString);
            }
            if (imageURL != null && !thanaDistrict.isEmpty()) {
                mThanaAndDistrictTextView.setText(thanaDistrict);
            }

            if (mobileNumber != null) {
                mMobileNumberEditText.setText(mobileNumber);
            }


        } else if (requestCode == PAYMENT_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
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
        String errorMessage;

        if (!Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        } else if (PaymentActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            //validation check of amount
            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else if (!InputValidator.isValidDigit(mAmountEditText.getText().toString().trim())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else {
                final BigDecimal paymentAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (paymentAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                } else {
                    final BigDecimal minimumPaymentAmount = PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumPaymentAmount = PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), paymentAmount, minimumPaymentAmount, maximumPaymentAmount);
                }
            }
        } else {
            errorMessage = getString(R.string.balance_not_available);
        }

        if (errorMessage != null) {
            focusView = mAmountEditText;
            mAmountEditText.setError(errorMessage);
            cancel = true;
        }
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

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

        if (TextUtils.isEmpty(mReceiverMobileNumber)) {
            mReceiver = mMobileNumberEditText.getText().toString().trim();
        } else {
            mReceiver = mReceiverMobileNumber;
        }
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String referenceNumber = mRefNumberEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        Intent intent = new Intent(getActivity(), PaymentReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(mReceiver));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);
        intent.putExtra(Constants.REFERENCE_NUMBER, referenceNumber);
        intent.putExtra(Constants.ADDRESS, mAddressString);
        //intent.putExtra(Constants.COUNTRY, mCountry);
        intent.putExtra(Constants.DISTRICT, mDistrict);
        intent.putExtra(Constants.THANA, mThana);
        intent.putExtra(Constants.PHOTO_URI, mReceiverPhotoUri);
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

    private void attemptPaymentWithPinCheck() {
        if (PaymentActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptPayment(pin);
                }
            });
        } else {
            attemptPayment(null);
        }
    }

    private void attemptPayment(String pin) {

        if (TextUtils.isEmpty(mReceiverMobileNumber)) {
            mReceiver = mMobileNumberEditText.getText().toString().trim();
        } else {
            mReceiver = mReceiverMobileNumber;
        }
        mAmount = mAmountEditText.getText().toString().trim();
        String referenceNumber = mRefNumberEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();

        if (mPaymentTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.progress_dialog_text_payment));
        mCustomProgressDialog.showDialog();
        mPaymentRequest = new PaymentRequest(
                ContactEngine.formatMobileNumberBD(mReceiver),
                mAmount, description, pin, referenceNumber, latitude, longitude);

        Gson gson = new Gson();
        String json = gson.toJson(mPaymentRequest);
        mPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT, json, getActivity(), false);
        mPaymentTask.mHttpResponseListener = this;
        mPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @SuppressLint("MissingPermission")
    private void getLocationAndLaunchReviewPage() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mProgressDialog.setMessage(getString(R.string.please_wait_loading));
            mProgressDialog.show();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this, Looper.getMainLooper());
        } else {
            Utilities.showGPSHighAccuracyDialog(this);
        }
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }
        getProfileInfo(mobileNumber, true);
    }

    private void getProfileInfo(String mobileNumber, boolean showProgressDialog) {
        if (mGetProfileInfoTask != null) {
            return;
        }
        mAddressProgressBar.setVisibility(View.VISIBLE);
        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getContext(), this, false);
        if (showProgressDialog) {
            mProgressDialog.setMessage(getActivity().getString(R.string.loading));
            mProgressDialog.setMessage(getString(R.string.please_wait_loading));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null)
            return;

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this, false);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        mProgressDialog.dismiss();

        attemptPaymentWithPinCheck();

        if (locationManager != null)
            locationManager.removeUpdates(this);
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
        Gson gson = new Gson();

        if (HttpErrorHandler.isErrorFound(result, getContext(), mCustomProgressDialog)) {
            mGetBusinessRuleTask = null;
            mGetProfileInfoTask = null;
            mPaymentTask = null;
            return;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
            mProgressDialog.dismiss();
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {
                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT)) {
                                PaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT)) {
                                PaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_VERIFICATION_REQUIRED)) {
                                PaymentActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_PIN_REQUIRED)) {
                                PaymentActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_LOCATION_REQUIRED)) {
                                PaymentActivity.mMandatoryBusinessRules.setLOCATION_REQUIRED(rule.getRuleValue());
                            }
                        }
                    }
                    BusinessRuleCacheManager.setBusinessRules(Constants.MAKE_PAYMENT, PaymentActivity.mMandatoryBusinessRules);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mGetBusinessRuleTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {

            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mAddressProgressBar.setVisibility(View.GONE);
                    mobileNumberView.setVisibility(GONE);
                    profileView.setVisibility(View.VISIBLE);
                    mReceiverName = mGetUserInfoResponse.getName();
                    if (mGetUserInfoResponse.getAddressList() != null) {
                        if (mGetUserInfoResponse.getAddressList().getOFFICE() != null) {
                            List<UserAddress> office = mGetUserInfoResponse.getAddressList().getOFFICE();
                            if (office != null) {
                                mAddressString = office.get(0).getAddressLine1();
                                mDistrict = office.get(0).getDistrict();
                                //mCountry = Utilities.getFormattedCountryName(office.get(0).getCountry());
                                mThana = office.get(0).getThana();
                                mAddressTextView.setText(mAddressString);
                                mThanaAndDistrictTextView.setText(mThana + " , " + mDistrict);
                                mAddressTextView.setVisibility(View.VISIBLE);
                                mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    int accountType = mGetUserInfoResponse.getAccountType();

                    if (accountType != Constants.BUSINESS_ACCOUNT_TYPE) {
                        new AlertDialog.Builder(getContext())
                                .setMessage(R.string.not_a_business_user)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mIconEditMobileNumber.callOnClick();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    } else if (accountType == Constants.BUSINESS_ACCOUNT_TYPE && !mGetUserInfoResponse.getAccountStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                        MaterialDialog materialDialog;
                        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());
                        materialDialogBuilder.positiveText(R.string.ok);
                        materialDialogBuilder.content(getString(R.string.business_account_not_verified));
                        materialDialogBuilder.dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                mIconEditMobileNumber.callOnClick();

                            }
                        });
                        materialDialog = materialDialogBuilder.build();
                        materialDialog.show();
                    }


                    String profilePicture = null;
                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                        businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePicture, false);
                    }

                    if (!TextUtils.isEmpty(profilePicture) && mReceiverPhotoUri == null) {
                        mReceiverPhotoUri = profilePicture;
                        businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePicture, false);
                    }
                    if (TextUtils.isEmpty(mReceiverName)) {
                        businessNameTextView.setVisibility(GONE);
                    } else {
                        businessNameTextView.setVisibility(View.VISIBLE);
                        businessNameTextView.setText(mReceiverName);
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_PAYMENT)) {

            try {
                PaymentResponse mPaymentResponse = gson.fromJson(result.getJsonString(), PaymentResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                        mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                    } else {
                        mCustomProgressDialog.showSuccessAnimationAndMessage(mPaymentResponse.getMessage());
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mCustomProgressDialog.dismissDialog();
                            getActivity().finish();
                        }
                    }, 2000);

                    Utilities.sendSuccessEventTracker(mTracker, "Make Payment", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());

                    //getActivity().finish();
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                    if (getActivity() != null) {
                        mCustomProgressDialog.showFailureAnimationAndMessage(mPaymentResponse.getMessage());
                        ((MyApplication) getActivity().getApplication()).launchLoginPage("");
                    }
                    Utilities.sendBlockedEventTracker(mTracker, "Make Payment", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                    mCustomProgressDialog.dismissDialog();
                    Toast.makeText(getActivity(), mPaymentResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    SecuritySettingsActivity.otpDuration = mPaymentResponse.getOtpValidFor();
                    launchOTPVerification();
                } else {
                    if (getActivity() != null) {
                        if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                            mCustomProgressDialog.showFailureAnimationAndMessage(mPaymentResponse.getMessage());
                        } else {
                            Toast.makeText(mContext, mPaymentResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        if (mPaymentResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.showOtpDialog();
                                mCustomProgressDialog.dismissDialog();
                            }
                        } else {
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                            }
                        }
                    }
                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Make Payment", ProfileInfoCacheManager.getAccountId(), mPaymentResponse.getMessage(), new BigDecimal(mAmount).longValue());

                }
            } catch (Exception e) {
                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                }
                e.printStackTrace();
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                mCustomProgressDialog.showFailureAnimationAndMessage(getString(R.string.service_not_available));
            }

            mProgressDialog.dismiss();
            mPaymentTask = null;

        }
    }

    private void switchToPaymentSuccessFragment(String name, String profilePictureUrl, String tansactionId) {
        PaymentSucessFragment paymentSuccessFragment = new PaymentSucessFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.NAME, name);
        bundle.putString(Constants.PHOTO_URI, profilePictureUrl);
        bundle.putString(Constants.TRANSACTION_ID, tansactionId);
        bundle.putString(Constants.AMOUNT, mAmount);
        paymentSuccessFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentSuccessFragment).commit();

    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mPaymentRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_PAYMENT,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
    }

    private List<BusinessContact> getBusinessContactList(Cursor cursor) {
        List<BusinessContact> mBusinessContacts;
        int businessNameIndex;
        int phoneNumberIndex;
        int profilePictureUrlIndex;
        int businessTypeIndex;
        int businessAddressIndex;
        int businessThanaIndex;
        int businessDistrictIndex;


        mBusinessContacts = new ArrayList<>();

        if (cursor != null) {
            mBusinessContacts.clear();
            businessNameIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_NAME);
            phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_PROFILE_PICTURE);
            businessTypeIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_TYPE);
            businessAddressIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_ADDRESS);
            businessThanaIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_THANA);
            businessDistrictIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_DISTRICT);

            if (cursor.moveToFirst())
                do {
                    String businessName = cursor.getString(businessNameIndex);
                    String mobileNumber = cursor.getString(phoneNumberIndex);
                    String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                    int businessTypeID = cursor.getInt(businessTypeIndex);
                    String businessAddress = cursor.getString(businessAddressIndex);
                    String businessThana = cursor.getString(businessThanaIndex);
                    String businessDistrict = cursor.getString(businessDistrictIndex);

                    BusinessContact businessContact = new BusinessContact();
                    businessContact.setBusinessName(businessName);
                    businessContact.setMobileNumber(mobileNumber);
                    businessContact.setProfilePictureUrl(profilePictureUrl);
                    businessContact.setAddressString(businessAddress);
                    businessContact.setThanaString(businessThana);
                    businessContact.setDistrictString(businessDistrict);

                    if (CommonData.getBusinessTypes() != null) {
                        BusinessType businessType = CommonData.getBusinessTypeById(businessTypeID);
                        if (businessType != null)
                            businessContact.setBusinessType(businessType.getName());
                    }

                    mBusinessContacts.add(businessContact);

                } while (cursor.moveToNext());
        }

        return mBusinessContacts;
    }

    private Cursor searchContact(String query){
        Cursor mCursor;
        DataHelper dataHelper = DataHelper.getInstance(getContext());
        mCursor = dataHelper.searchBusinessAccountsByMobile(query.replaceAll("[^0-9]", ""));
        return mCursor;
    }

    private void setValueFromCursor(Cursor cursor){
        mMobileNumberEditText.setText("");
        List<BusinessContact> mBusinessContactList = getBusinessContactList(cursor);
        BusinessContact mBussinessContact = mBusinessContactList.get(0);

        mAddressProgressBar.setVisibility(View.GONE);
        mobileNumberView.setVisibility(GONE);
        profileView.setVisibility(View.VISIBLE);
        mReceiverName = mBussinessContact.getBusinessName();
        mAddressString = mBussinessContact.getAddressString();
        mDistrict = mBussinessContact.getDistrictString();
        mThana = mBussinessContact.getThanaString();
        mAddressTextView.setText(mAddressString);
        mThanaAndDistrictTextView.setText(mThana + " , " + mDistrict);
        mAddressTextView.setVisibility(View.VISIBLE);
        mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
        mReceiverMobileNumber = ContactEngine.formatMobileNumberBD(mBussinessContact.getMobileNumber());
        mMobileNumberEditText.setText(mBussinessContact.getMobileNumber());

        if (TextUtils.isEmpty(mReceiverName)) {
            businessNameTextView.setVisibility(GONE);
        } else {
            businessNameTextView.setVisibility(View.VISIBLE);
            businessNameTextView.setText(mReceiverName);
        }

        String profilePicture = mBussinessContact.getProfilePictureUrl();
        if (!profilePicture.isEmpty()) {
            mReceiverPhotoUri = profilePicture;
            businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
        }
    }

}
