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
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.BusinessOutletSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.BusinessContact;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.GetAllBusinessContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.BusinessContact.Outlets;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRuleV2;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.Rule;
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
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
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
    private EditText mOutletEditText;
    private EditText mRefNumberEditText;
    private TextView mBalanceView;
    private View profileView;
    private View mobileNumberView;
    private View outletView;
    private ProgressDialog mProgressDialog;
    private ProgressBar mAddressProgressBar;

    private CustomProgressDialog mCustomProgressDialog;

    private ProfileImageView businessProfileImageView;
    private TextView businessNameTextView;
    private TextView outletNameTextView;
    private TextView businessMobileNumberTextView;
    private TextView mAddressTextView;
    private TextView mThanaAndDistrictTextView;
    private View mIconEditMobileNumber;


    private String mReceiverMobileNumber;
    private String mReceiverName;
    private String mReceiverPhotoUri;
    private String mAddressString;
    private String mDistrict;
    private String mThana;
    private String mAmount;
    private String mReceiver;
    private Long mOutletId;
    private String mOutletName;

    private double latitude = 0.0;
    private double longitude = 0.0;

    private Context mContext;

    private String mPin;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private LocationManager locationManager;
    private BusinessOutletSelectorDialog moutletSelectorDialog;
    List<Outlets> outlets;
    private boolean hasOutlet = false;

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
        outletView = v.findViewById(R.id.outlet_holder);
        mobileNumberView = v.findViewById(R.id.mobile_number_holder);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mOutletEditText = (EditText) v.findViewById(R.id.outlet_selector_edit_text);
        mRefNumberEditText = (EditText) v.findViewById(R.id.reference_number);
        mAddressTextView = (TextView) v.findViewById(R.id.textview_address_line_1);
        mThanaAndDistrictTextView = (TextView) v.findViewById(R.id.textview_address_line_2);
        mProgressDialog = new ProgressDialog(getActivity());

        businessProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        businessProfileImageView.setBusinessLogoPlaceHolder();
        businessNameTextView = (TextView) v.findViewById(R.id.textview_name);
        outletNameTextView = (TextView) v.findViewById(R.id.textview_outlet_name);
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
        if (getActivity().getIntent().hasExtra(Constants.FROM_BRANCHING)) {
            mIconEditMobileNumber.setVisibility(GONE);
            try {
                mobileNumberView.setVisibility(GONE);
                profileView.setVisibility(View.VISIBLE);
                if (getActivity().getIntent().hasExtra(Constants.PHOTO_URI)) {
                    mReceiverPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                    businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                }
                mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);
                mMobileNumberEditText.setText(mReceiverMobileNumber);
                if (getActivity().getIntent().hasExtra(Constants.OUTLET_ID)) {
                    mOutletId = getActivity().getIntent().getLongExtra(Constants.OUTLET_ID, 0);
                } else {
                    mOutletId = null;
                }

                if (getActivity().getIntent().hasExtra(Constants.NAME)) {
                    mReceiverName = getActivity().getIntent().getStringExtra(Constants.NAME);
                    if (TextUtils.isEmpty(mReceiverName)) {
                        businessNameTextView.setVisibility(GONE);
                    } else {
                        businessNameTextView.setVisibility(View.VISIBLE);
                        businessNameTextView.setText(mReceiverName);
                    }
                }
                if (getActivity().getIntent().hasExtra(Constants.OUTLET_NAME)) {
                    mOutletName = getActivity().getIntent().getStringExtra(Constants.OUTLET_NAME);
                    if (TextUtils.isEmpty(mOutletName)) {
                        outletNameTextView.setVisibility(GONE);
                    } else {
                        outletNameTextView.setVisibility(View.VISIBLE);
                        outletNameTextView.setText(mOutletName);
                    }
                } else {
                    outletNameTextView.setVisibility(GONE);
                }

                if (getActivity().getIntent().hasExtra(Constants.ADDRESS)) {
                    mAddressString = getActivity().getIntent().getStringExtra(Constants.ADDRESS);
                    if (mAddressString != null) {
                        mAddressTextView.setVisibility(View.VISIBLE);
                        mAddressTextView.setText(mAddressString);
                    } else {
                        mAddressTextView.setVisibility(View.GONE);
                    }
                }

                if (getActivity().getIntent().hasExtra(Constants.DISTRICT)) {
                    String thanaDistrict = getActivity().getIntent().getStringExtra(Constants.DISTRICT);
                    if (getActivity().getIntent().hasExtra(Constants.THANA)) {
                        thanaDistrict = getActivity().getIntent().getStringExtra(Constants.THANA) + ", " + thanaDistrict;
                    }
                    if (thanaDistrict != null) {
                        mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
                        mThanaAndDistrictTextView.setText(thanaDistrict);
                    } else {
                        mThanaAndDistrictTextView.setVisibility(View.GONE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
                mIconEditMobileNumber.setVisibility(View.VISIBLE);
                mobileNumberView.setVisibility(GONE);
                profileView.setVisibility(View.VISIBLE);
                if (getActivity().getIntent().hasExtra(Constants.PHOTO_URI)) {
                    mReceiverPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                    businessProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                }
                if (getActivity().getIntent().hasExtra(Constants.FROM_QR_SCAN)) {
                    if (getActivity().getIntent().getBooleanExtra(Constants.FROM_QR_SCAN, false)) {
                        mIconEditMobileNumber.setVisibility(GONE);
                    }
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
                        mDistrict = getActivity().getIntent().getStringExtra(Constants.DISTRICT);
                        mThana = getActivity().getIntent().getStringExtra(Constants.THANA);
                        mReceiverPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                        if (getActivity().getIntent().hasExtra(Constants.OUTLET_ID)) {
                            mOutletId = getActivity().getIntent().getLongExtra(Constants.OUTLET_ID, 0);
                        } else {
                            mOutletId = null;
                        }

                        mAddressTextView.setVisibility(View.VISIBLE);
                        mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
                        mAddressTextView.setText(mAddressString);
                        mThanaAndDistrictTextView.setText(mThana + ", " + mDistrict);
                        if (mReceiverPhotoUri != null) {
                            businessProfileImageView.setBusinessProfilePicture
                                    (Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                        }

                        if (getActivity().getIntent().hasExtra(Constants.OUTLET_NAME)) {
                            mOutletName = getActivity().getIntent().getStringExtra(Constants.OUTLET_NAME);
                            if (TextUtils.isEmpty(mOutletName)) {
                                outletNameTextView.setVisibility(GONE);
                            } else {
                                outletNameTextView.setVisibility(View.VISIBLE);
                                outletNameTextView.setText(mOutletName);
                            }
                        }
                    } else if (getArguments() != null) {
                        try {
                            mAddressString = getArguments().getString(Constants.ADDRESS);
                            mDistrict = getArguments().getString(Constants.DISTRICT);
                            mThana = getArguments().getString(Constants.THANA);
                            mOutletId = getArguments().getLong(Constants.OUTLET_ID);

                            if (mAddressString != null) {
                                mAddressTextView.setText(mAddressString);
                                mThanaAndDistrictTextView.setText(mThana + ", " + mDistrict);
                                mAddressTextView.setVisibility(View.VISIBLE);
                                mThanaAndDistrictTextView.setVisibility(View.VISIBLE);

                            }
                            if (getArguments().getString(Constants.PHOTO_URI) != null) {
                                mReceiverPhotoUri = getArguments().getString(Constants.PHOTO_URI);
                                businessProfileImageView.setBusinessProfilePicture
                                        (Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                            }

                            if (getArguments().getString(Constants.OUTLET_NAME) != null) {
                                mOutletName = getArguments().getString(Constants.OUTLET_NAME);
                                if (TextUtils.isEmpty(mOutletName)) {
                                    outletNameTextView.setVisibility(GONE);
                                } else {
                                    outletNameTextView.setVisibility(View.VISIBLE);
                                    outletNameTextView.setText(mOutletName);
                                }
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
                outletView.setVisibility(GONE);
                mOutletEditText.getText().clear();
                mOutletName = null;
                mOutletId = null;
                mMobileNumberEditText.requestFocus();
            }
        });

        outletView.setVisibility(GONE);
        mOutletEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (outlets.size() > 0) {
                    moutletSelectorDialog = new BusinessOutletSelectorDialog(getContext(), mReceiverName, mReceiverPhotoUri, outlets);//dfgdfgdfg
                    moutletSelectorDialog.setOnResourceSelectedListener(new BusinessOutletSelectorDialog.OnResourceSelectedListener() {
                        @Override
                        public void onResourceSelected(Outlets allowablePackage) {
                            mOutletEditText.setText(allowablePackage.getOutletName());
                            mOutletId = allowablePackage.getOutletId();
                            mobileNumberView.setVisibility(View.GONE);
                            profileView.setVisibility(View.VISIBLE);
                            mAddressTextView.setVisibility(View.VISIBLE);
                            mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
                            String thanaDistrict = allowablePackage.getOutletAddress().getThanaName() + ", " + allowablePackage.getOutletAddress().getDistrictName();

                            if (allowablePackage.getOutletLogoUrl() != null && !allowablePackage.getOutletLogoUrl().isEmpty()) {
                                mReceiverPhotoUri = allowablePackage.getOutletLogoUrl();
                                businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                            }
                            if (!allowablePackage.getOutletName().isEmpty()) {
                                mOutletName = allowablePackage.getOutletName();
                                outletNameTextView.setVisibility(View.VISIBLE);
                                outletNameTextView.setText(mOutletName);
                            } else {
                                outletNameTextView.setVisibility(View.GONE);
                            }
                            if (allowablePackage.getAddressString() != null && !allowablePackage.getAddressString().isEmpty()) {
                                mAddressString = allowablePackage.getAddressString();
                                mAddressTextView.setText(mAddressString);
                            }
                            if (thanaDistrict != null && !thanaDistrict.isEmpty()) {
                                mThanaAndDistrictTextView.setText(thanaDistrict);
                            }
                            outletView.setVisibility(View.GONE);
                        }
                    });
                    moutletSelectorDialog.showDialog();
                }
            }
        });

        mMobileNumberEditText.setOnCustomFocusChangeListener(new BusinessContactsSearchView.CustomFocusListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    profileView.setVisibility(GONE);
                    outletView.setVisibility(GONE);
                    mOutletEditText.getText().clear();
                    mOutletName = null;
                    mOutletId = null;
                    hasOutlet = false;
                }
            }
        });


        mMobileNumberEditText.setCustomTextChangeListener(new BusinessContactsSearchView.CustomTextChangeListener() {
            @Override
            public void onTextChange(String inputText) {
                if (profileView.getVisibility() == GONE
                        && Utilities.isConnectionAvailable(getActivity())
                        && InputValidator.isValidNumber(inputText)) {
                    if (!hasOutlet) {
                        Cursor mCursor = searchContact(inputText);
                        try {
                            if (mCursor != null && mCursor.getCount() > 0) {
                                setValueFromCursor(mCursor);
                            } else {
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

                    } else {
                        outletView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTextChange(String inputText, String name, String imageURL, String address, String thanaDistrict, String outlet) {

                if (TextUtils.isEmpty(name)) {
                    businessNameTextView.setVisibility(GONE);
                } else {
                    businessNameTextView.setVisibility(View.VISIBLE);
                    businessNameTextView.setText(name);
                }

                if (!TextUtils.isEmpty(outlet)) {
                    hasOutlet = true;
                    Type outletListType = new TypeToken<ArrayList<Outlets>>() {
                    }.getType();
                    outlets = new Gson().fromJson(outlet, outletListType);
                    mReceiverName = name;
                    mReceiverPhotoUri = imageURL;
                    outletView.setVisibility(View.VISIBLE);
                    mMobileNumberEditText.setText(inputText);
                } else {
                    hasOutlet = false;
                    outletView.setVisibility(View.GONE);
                    outletNameTextView.setVisibility(View.GONE);
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
                    mMobileNumberEditText.clearSelectedData();
                }
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
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {

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
                    String[] stringArray = result.split("-");
                    final String mobile = stringArray[0];
                    if (stringArray.length > 1) {
                        try {
                            mOutletId = Long.parseLong(stringArray[1].trim().replaceAll("[^0-9]", ""));
                        } catch (Exception e) {
                            mOutletId = null;
                        }
                    } else {
                        mOutletId = null;
                    }

                    if (mobile != null) {
                        Handler mHandler = new Handler();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (InputValidator.isValidNumber(mobile)) {
                                    mReceiverMobileNumber = ContactEngine.formatMobileNumberBD(mobile);
                                    Cursor mCursor = searchContact(mReceiverMobileNumber);
                                    try {
                                        if (mCursor != null) {
                                            setValueFromCursor(mCursor, mOutletId);
                                        } else {
                                            mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
                                            getProfileInfo(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
                                        getProfileInfo(ContactEngine.formatMobileNumberBD(mReceiverMobileNumber));
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
            outletView.setVisibility(GONE);
            mOutletId = null;
            mOutletName = null;
            outletNameTextView.setVisibility(GONE);
            mOutletEditText.getText().clear();
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            String name = data.getStringExtra(Constants.BUSINESS_NAME);
            String imageURL = data.getStringExtra(Constants.PROFILE_PICTURE);
            String address = data.getStringExtra(Constants.ADDRESS);
            String thanaDistrict = data.getStringExtra(Constants.THANA) + ", " + data.getStringExtra(Constants.DISTRICT);
            String outletString = data.getStringExtra(Constants.OUTLET);
            if (!name.isEmpty()) {
                mReceiverName = name;
                businessNameTextView.setText(mReceiverName);
            }

            if (!TextUtils.isEmpty(outletString)) {
                hasOutlet = true;
                Type outletListType = new TypeToken<ArrayList<Outlets>>() {
                }.getType();
                outlets = new Gson().fromJson(outletString, outletListType);
                mReceiverName = name;
                mReceiverPhotoUri = imageURL;
                outletView.setVisibility(View.VISIBLE);
                mMobileNumberEditText.setText(mobileNumber);
            } else {
                hasOutlet = false;
                outletView.setVisibility(View.GONE);
                outletNameTextView.setVisibility(View.GONE);
                mobileNumberView.setVisibility(View.GONE);
                profileView.setVisibility(View.VISIBLE);
                mAddressTextView.setVisibility(View.VISIBLE);
                mThanaAndDistrictTextView.setVisibility(View.VISIBLE);

                if (imageURL != null && !imageURL.isEmpty()) {
                    mReceiverPhotoUri = imageURL;
                    businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
                }

                if (address != null && !address.isEmpty()) {
                    mAddressString = address;
                    mAddressTextView.setText(mAddressString);
                }
                if (thanaDistrict != null && !thanaDistrict.isEmpty()) {
                    mThanaAndDistrictTextView.setText(thanaDistrict);
                }

                if (mobileNumber != null) {
                    mMobileNumberEditText.setText(mobileNumber);
                }
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

        if (hasOutlet && mOutletId == null) {
            errorMessage = "Please select a outlet.";
            focusView = mOutletEditText;
            mOutletEditText.setError(errorMessage);
            cancel = true;
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

        mPin = pin;
        mReceiver = ContactEngine.formatMobileNumberBD(mMobileNumberEditText.getText().toString().trim());
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
                mAmount, description, referenceNumber, mOutletId, latitude, longitude);

        Gson gson = new Gson();
        String json = gson.toJson(mPaymentRequest);
        mPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_V3, json, getActivity(), false);
        mPaymentTask.setPinAsHeader(pin);
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
        mCustomProgressDialog.setLoadingMessage(getString(R.string.please_wait_loading));
        mCustomProgressDialog.showDialog();
        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getContext(), this, false);
        mGetProfileInfoTask.mHttpResponseListener = this;
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
                    BusinessRuleV2 businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRuleV2.class);
                    List<Rule> rules = businessRuleArray.getRules();

                    for (Rule rule : rules) {
                        switch (rule.getRuleName()) {
                            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT_V3:
                                PaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                break;
                            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT_V3:
                                PaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                break;
                            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_VERIFICATION_REQUIRED_V3:
                                PaymentActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                break;
                            case BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_PIN_REQUIRED_V3:
                                PaymentActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                                break;
                        }
                    }

                    BusinessRuleCacheManager.setBusinessRules(Constants.MAKE_PAYMENT, PaymentActivity.mMandatoryBusinessRules);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mGetBusinessRuleTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            mCustomProgressDialog.dismissDialog();

            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mAddressProgressBar.setVisibility(View.GONE);
                    mobileNumberView.setVisibility(GONE);
                    profileView.setVisibility(View.VISIBLE);
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

                    if (!mGetUserInfoResponse.getName().isEmpty())
                        mReceiverName = mGetUserInfoResponse.getName();
                    String profilePicture = null;

                    if (mGetUserInfoResponse.getOutlets() != null && mGetUserInfoResponse.getOutlets().size() > 0 && mGetUserInfoResponse.getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE) {
                        if (mOutletId != null) {
                            for (Outlets outlets : mGetUserInfoResponse.getOutlets()) {
                                if (outlets.getOutletId().equals(mOutletId)) {
                                    profilePicture = outlets.getOutletLogoUrl();
                                    mAddressString = outlets.getAddressString();
                                    mDistrict = outlets.getOutletAddress().getDistrictName();
                                    mThana = outlets.getOutletAddress().getThanaName();
                                    mOutletName = outlets.getOutletName();

                                    mAddressTextView.setText(mAddressString);
                                    mThanaAndDistrictTextView.setText(mThana + " , " + mDistrict);
                                    mAddressTextView.setVisibility(View.VISIBLE);
                                    mThanaAndDistrictTextView.setVisibility(View.VISIBLE);

                                    if (TextUtils.isEmpty(mOutletName)) {
                                        outletNameTextView.setVisibility(GONE);
                                    } else {
                                        outletNameTextView.setVisibility(View.VISIBLE);
                                        outletNameTextView.setText(mOutletName);
                                    }

                                    break;
                                }
                            }
                        } else {
                            hasOutlet = true;
                            mobileNumberView.setVisibility(View.VISIBLE);
                            profileView.setVisibility(View.GONE);
                            outlets = mGetUserInfoResponse.getOutlets();
                            mReceiverName = mGetUserInfoResponse.getName();
                            mReceiverPhotoUri = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                            outletView.setVisibility(View.VISIBLE);
                            mMobileNumberEditText.setText(mReceiverMobileNumber);
                        }
                    } else {

                        outletNameTextView.setVisibility(GONE);
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

                        if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                            profilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                        }
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
                final PaymentResponse mPaymentResponse = gson.fromJson(result.getJsonString(), PaymentResponse.class);

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
                            switchToPaymentSuccessFragment(mReceiverName, mOutletName, mReceiverPhotoUri, mPaymentResponse.getTransactionId());
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

    private void switchToPaymentSuccessFragment(String name, String outletName, String profilePictureUrl, String tansactionId) {
        PaymentSucessFragment paymentSuccessFragment = new PaymentSucessFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.NAME, name);
        bundle.putString(Constants.OUTLET_NAME, outletName);
        bundle.putString(Constants.PHOTO_URI, profilePictureUrl);
        bundle.putString(Constants.TRANSACTION_ID, tansactionId);
        bundle.putString(Constants.AMOUNT, mAmount);
        paymentSuccessFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentSuccessFragment).commit();

    }

    private void launchOTPVerification() {
        mPaymentRequest.setPin(mPin);
        String jsonString = new Gson().toJson(mPaymentRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_PAYMENT,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_V3, Constants.METHOD_POST);
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
        int businessOutletIndex;

        mBusinessContacts = new ArrayList<>();

        if (cursor != null && cursor.getCount() > 0) {
            mBusinessContacts.clear();
            businessNameIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_NAME);
            phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
            profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_PROFILE_PICTURE);
            businessTypeIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_TYPE);
            businessAddressIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_ADDRESS);
            businessThanaIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_THANA);
            businessDistrictIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_DISTRICT);
            businessOutletIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_OUTLET);

            if (cursor.moveToFirst())
                do {
                    String businessName = cursor.getString(businessNameIndex);
                    String mobileNumber = cursor.getString(phoneNumberIndex);
                    String profilePictureUrl = cursor.getString(profilePictureUrlIndex);
                    int businessTypeID = cursor.getInt(businessTypeIndex);
                    String businessAddress = cursor.getString(businessAddressIndex);
                    String businessThana = cursor.getString(businessThanaIndex);
                    String businessDistrict = cursor.getString(businessDistrictIndex);
                    String businessOutlet = cursor.getString(businessOutletIndex);

                    BusinessContact businessContact = new BusinessContact();
                    businessContact.setBusinessName(businessName);
                    businessContact.setMobileNumber(mobileNumber);
                    businessContact.setProfilePictureUrl(profilePictureUrl);
                    businessContact.setAddressString(businessAddress);
                    businessContact.setThanaString(businessThana);
                    businessContact.setDistrictString(businessDistrict);
                    businessContact.setOutletString(businessOutlet);

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

    private Cursor searchContact(String query) {
        Cursor mCursor;
        DataHelper dataHelper = DataHelper.getInstance(getContext());
        mCursor = dataHelper.searchBusinessAccountsByMobile(query.replaceAll("[^0-9]", ""));
        return mCursor;
    }

    private void setValueFromCursor(Cursor cursor) {
        mMobileNumberEditText.setText("");
        List<BusinessContact> mBusinessContactList = getBusinessContactList(cursor);
        BusinessContact mBussinessContact = mBusinessContactList.get(0);
        outletNameTextView.setVisibility(GONE);
        mOutletEditText.getText().clear();
        mAddressProgressBar.setVisibility(View.GONE);

        mReceiverName = mBussinessContact.getBusinessName();
        if (TextUtils.isEmpty(mReceiverName)) {
            businessNameTextView.setVisibility(GONE);
        } else {
            businessNameTextView.setVisibility(View.VISIBLE);
            businessNameTextView.setText(mReceiverName);
        }

        if (!TextUtils.isEmpty(mBussinessContact.getOutletString())) {
            hasOutlet = true;
            Type outletListType = new TypeToken<ArrayList<Outlets>>() {
            }.getType();
            outlets = new Gson().fromJson(mBussinessContact.getOutletString(), outletListType);
            mReceiverName = mBussinessContact.getBusinessName();
            mReceiverPhotoUri = mBussinessContact.getProfilePictureUrl();
            outletView.setVisibility(View.VISIBLE);
            mMobileNumberEditText.setText(mBussinessContact.getMobileNumber());
        } else {
            hasOutlet = false;
            outletView.setVisibility(View.GONE);
            outletNameTextView.setVisibility(View.GONE);
            mobileNumberView.setVisibility(View.GONE);
            profileView.setVisibility(View.VISIBLE);
            mAddressTextView.setVisibility(View.VISIBLE);
            mThanaAndDistrictTextView.setVisibility(View.VISIBLE);

            mAddressString = mBussinessContact.getAddressString();
            mDistrict = mBussinessContact.getDistrictString();
            mThana = mBussinessContact.getThanaString();
            mAddressTextView.setText(mAddressString);
            mThanaAndDistrictTextView.setText(mThana + " , " + mDistrict);
            mAddressTextView.setVisibility(View.VISIBLE);
            mThanaAndDistrictTextView.setVisibility(View.VISIBLE);
            mReceiverMobileNumber = ContactEngine.formatMobileNumberBD(mBussinessContact.getMobileNumber());
            mMobileNumberEditText.setText(mBussinessContact.getMobileNumber());

            String profilePicture = mBussinessContact.getProfilePictureUrl();
            if (!profilePicture.isEmpty()) {
                mReceiverPhotoUri = profilePicture;
                businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
            }
        }


    }


    private void setValueFromCursor(Cursor cursor, Long outletId) {
        mMobileNumberEditText.setText("");
        List<BusinessContact> mBusinessContactList = getBusinessContactList(cursor);
        BusinessContact mBussinessContact = mBusinessContactList.get(0);
        outletView.setVisibility(View.GONE);
        mAddressProgressBar.setVisibility(View.GONE);
        mobileNumberView.setVisibility(GONE);
        profileView.setVisibility(View.VISIBLE);
        mReceiverName = mBussinessContact.getBusinessName();

        if (outletId != null) {
            if (!mBussinessContact.getOutletString().isEmpty()) {
                Outlets[] outlets = new Gson().fromJson(mBussinessContact.getOutletString(), Outlets[].class);
                for (Outlets outlet : outlets) {
                    if (outlet.getOutletId().equals(outletId)) {
                        mOutletName = outlet.getOutletName();
                        mReceiverPhotoUri = outlet.getOutletLogoUrl();
                        mAddressString = outlet.getAddressString();
                        mDistrict = outlet.getOutletAddress().getDistrictName();
                        mThana = outlet.getOutletAddress().getThanaName();
                    }
                }
            }

        } else {
            if (!mBussinessContact.getProfilePictureUrl().isEmpty())
                mReceiverPhotoUri = mBussinessContact.getProfilePictureUrl();
            if (!mBussinessContact.getAddressString().isEmpty())
                mAddressString = mBussinessContact.getAddressString();
            if (!mBussinessContact.getDistrictString().isEmpty())
                mDistrict = mBussinessContact.getDistrictString();
            if (!mBussinessContact.getThanaString().isEmpty())
                mThana = mBussinessContact.getThanaString();
        }

        if (TextUtils.isEmpty(mOutletName)) {
            outletNameTextView.setVisibility(GONE);
        } else {
            outletNameTextView.setVisibility(View.VISIBLE);
            outletNameTextView.setText(mOutletName);
        }

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
        if (!mReceiverPhotoUri.isEmpty()) {
            businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mReceiverPhotoUri, false);
        }
    }

}
