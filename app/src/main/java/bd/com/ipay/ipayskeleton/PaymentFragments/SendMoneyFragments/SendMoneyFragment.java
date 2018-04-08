package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.CustomContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyFragment extends BaseFragment implements HttpResponseListener {

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private final int PICK_CONTACT_REQUEST = 100;
    private final int SEND_MONEY_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;
    private HttpRequestGetAsyncTask mGetUserInfoTask;

    private HttpRequestPostAsyncTask mSendMoneyTask = null;
    private SendMoneyRequest mSendMoneyRequest;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private Button buttonSend;
    private ImageView buttonSelectFromContacts;
    private ImageView buttonScanQRCode;
    private CustomContactsSearchView mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private ProfileImageView mProfileImageView;
    private TextView mNameTextView;
    private View mProfilePicHolderView;
    private View mMobileNumberHolderView;
    private View mIconEditMobileNumber;
    private CheckBox addToContactCheckBox;
    private CustomProgressDialog mCustomProgressDialog;

    private String mReceiver;
    private String mAmount;
    private String mDescription;
    private String mSenderMobileNumber;
    private String mName;
    private String mProfilePicture;

    private String address;
    private String country;
    private String district;
    private String thana;
    private Context mContext;

    private ProgressDialog mProgressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_money, container, false);
        mMobileNumberEditText = (CustomContactsSearchView) v.findViewById(R.id.mobile_number);
        mNameTextView = (TextView) v.findViewById(R.id.receiver_name_text_view);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonScanQRCode = (ImageView) v.findViewById(R.id.button_scan_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        buttonSend = (Button) v.findViewById(R.id.button_send_money);
        mContext = getContext();
        mProfileImageView = (ProfileImageView) v.findViewById(R.id.receiver_profile_image_view);
        mProfilePicHolderView = v.findViewById(R.id.profile_pic_holder);
        mMobileNumberHolderView = v.findViewById(R.id.mobile_number_holder);
        mIconEditMobileNumber = v.findViewById(R.id.edit_icon_mobile_number);
        addToContactCheckBox = (CheckBox) v.findViewById(R.id.add_to_contact_check_box);
        mCustomProgressDialog = new CustomProgressDialog(getContext());
        mProgressDialog = new ProgressDialog(mContext);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        mMobileNumberEditText.setCurrentFragmentTag(Constants.SEND_MONEY);

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumberEditText.setText(getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER));
        }

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                intent.putExtra(Constants.IPAY_MEMBERS_ONLY, true);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        attemptSendMoneyWithPinCheck();
                        if (addToContactCheckBox.isChecked()) {
                            addContact(mName, mReceiver, null);
                        }
                    }
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
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
                mProfilePicHolderView.setVisibility(View.GONE);
                mMobileNumberHolderView.setVisibility(View.VISIBLE);
                mMobileNumberEditText.requestFocus();

                addToContactCheckBox.setVisibility(View.GONE);
                addToContactCheckBox.setChecked(false);
            }
        });

        mMobileNumberEditText.setCustomTextChangeListener(new ContactsSearchView.CustomTextChangeListener() {
            @Override
            public void onTextChange(String inputText) {
                if (mProfilePicHolderView.getVisibility() == View.GONE
                        && Utilities.isConnectionAvailable(getActivity())
                        && InputValidator.isValidNumber(inputText)) {
                    getUserInfo(ContactEngine.formatMobileNumberBD(inputText));
                }
            }

            @Override
            public void onTextChange(String inputText, String name, String imageURL) {
                mProfilePicHolderView.setVisibility(View.VISIBLE);
                mMobileNumberHolderView.setVisibility(View.GONE);

                if (!imageURL.isEmpty()) {
                    mProfileImageView.setProfilePicture(imageURL,
                            false);
                }
                if (!name.isEmpty()) {
                    mNameTextView.setText(name);
                }

                mMobileNumberEditText.clearSelectedData();
            }
        });

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumberHolderView.setVisibility(View.GONE);
            mProfilePicHolderView.setVisibility(View.VISIBLE);
            mReceiver = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);
            mMobileNumberEditText.setText(mReceiver);
            if (getActivity().getIntent().hasExtra(Constants.NAME)) {
                mName = getActivity().getIntent().getStringExtra(Constants.NAME);
                if (TextUtils.isEmpty(mName)) {
                    mNameTextView.setVisibility(View.GONE);
                } else {
                    mNameTextView.setVisibility(View.VISIBLE);
                    mNameTextView.setText(mName);
                }
            }
            if (getActivity().getIntent().hasExtra(Constants.PHOTO_URI)) {
                String imageURL = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
                if (!imageURL.isEmpty()) {
                    mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageURL,
                            false);
                }
            }
        } else {
            mProfilePicHolderView.setVisibility(View.GONE);
            mMobileNumberHolderView.setVisibility(View.VISIBLE);
        }

        // Get business rule

        attemptGetBusinessRule(Constants.SERVICE_ID_SEND_MONEY);


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_send_money));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                    startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
                } else {
                    Toaster.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG);
                }
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
                                    mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(result));
                                    if (Utilities.isConnectionAvailable(getActivity())) {
                                        String mobileNumber = ContactEngine.formatMobileNumberBD(result);
                                        getUserInfo(mobileNumber);
                                    } else {
                                        Toaster.makeText(getActivity(), getResources().getString(
                                                R.string.no_internet_connection), Toast.LENGTH_SHORT);
                                        mProgressDialog.cancel();
                                        getActivity().finish();
                                    }
                                } else if (getActivity() != null)
                                    Toaster.makeText(getActivity(), getResources().getString(
                                            R.string.scan_valid_ipay_qr_code), Toast.LENGTH_SHORT);
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
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            String name = data.getStringExtra(Constants.NAME);
            String imageURL = data.getStringExtra(Constants.PROFILE_PICTURE);

            if (mobileNumber != null)
                mMobileNumberEditText.setText(mobileNumber);

            mProfilePicHolderView.setVisibility(View.VISIBLE);
            mMobileNumberHolderView.setVisibility(View.GONE);

            if (!imageURL.isEmpty()) {
                mProfileImageView.setProfilePicture(imageURL,
                        false);
            }
            if (!name.isEmpty()) {
                mNameTextView.setText(name);
            }
        } else if (requestCode == SEND_MONEY_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage;
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        mReceiver = mMobileNumberEditText.getText().toString().trim();
        mAmount = mAmountEditText.getText().toString().trim();
        mDescription = mDescriptionEditText.getText().toString().trim();
        mSenderMobileNumber = ProfileInfoCacheManager.getMobileNumber();

        if (!Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        } else if (SendMoneyActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);

            } else {
                final BigDecimal sendMoneyAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (sendMoneyAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                } else {
                    final BigDecimal minimumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), sendMoneyAmount, minimumSendMoneyAmount, maximumSendMoneyAmount);
                }
            }
        } else {
            errorMessage = getString(R.string.balance_not_available);
        }

        if (errorMessage != null) {
            focusView = mAmountEditText;
            mAmountEditText.setError(errorMessage);
            cancel = true;
        } else if (!InputValidator.isValidNumber(mobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        } else if (ContactEngine.formatMobileNumberBD(mobileNumber).equals(ProfileInfoCacheManager.getMobileNumber())) {
            focusView = mMobileNumberEditText;
            DialogUtils.showDialogOwnNumberErrorDialog(getActivity(), mIconEditMobileNumber);
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void attemptSendMoneyWithPinCheck() {

        if (SendMoneyActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptSendMoney(pin);
                }
            });
        } else {
            attemptSendMoney(null);
        }
    }

    private void getUserInfo(String mobileNumber) {
        mReceiver = mobileNumber;


        GetUserInfoRequestBuilder getUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        if (mGetUserInfoTask != null) {
            return;
        }
        mCustomProgressDialog.setLoadingMessage(getString(R.string.please_wait));
        mCustomProgressDialog.showDialog();
        mGetUserInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                getUserInfoRequestBuilder.getGeneratedUri(), getActivity());
        mGetUserInfoTask.mHttpResponseListener = SendMoneyFragment.this;
        mGetUserInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @ValidateAccess
    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchReviewPage() {

        String receiver = mMobileNumberEditText.getText().toString().trim();
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String description = mDescriptionEditText.getText().toString().trim();

        Intent intent = new Intent(getActivity(), SendMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(receiver));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);
        intent.putExtra(Constants.IS_IN_CONTACTS, new ContactSearchHelper(getActivity()).searchMobileNumber(receiver));

        startActivityForResult(intent, SEND_MONEY_REVIEW_REQUEST);
    }

    private void attemptSendMoney(String pin) {
        if (mSendMoneyTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.progress_dialog_text_sending_money));
        mCustomProgressDialog.showDialog();

        mSendMoneyRequest = new SendMoneyRequest(
                mSenderMobileNumber, ContactEngine.formatMobileNumberBD(mReceiver),
                mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mSendMoneyRequest);
        mSendMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, json, getActivity());
        mSendMoneyTask.mHttpResponseListener = this;
        mSendMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null) {
            return;
        }
        mProgressDialog.setMessage("Fetching");
        mProgressDialog.show();
        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mSendMoneyRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        Gson gson = new Gson();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetUserInfoTask = null;
            mSendMoneyTask = null;
            mGetBusinessRuleTask = null;
            mCustomProgressDialog.dismissDialog();

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
            mCustomProgressDialog.dismissDialog();

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_VERIFICATION_REQUIRED)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_PIN_REQUIRED)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                }

            } else {
                if (getActivity() != null)
                    DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            }

            mGetBusinessRuleTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            mCustomProgressDialog.dismissDialog();
            try {
                GetUserInfoResponse mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mProfilePicHolderView.setVisibility(View.VISIBLE);
                    mMobileNumberHolderView.setVisibility(View.GONE);

                    if (!new ContactSearchHelper(getActivity()).searchMobileNumber(mReceiver)) {
                        addToContactCheckBox.setVisibility(View.VISIBLE);
                        addToContactCheckBox.setChecked(true);
                    }

                    mName = mGetUserInfoResponse.getName();

                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        mProfilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                        mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfilePicture,
                                false);
                    }
                    mNameTextView.setText(mName);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            mGetUserInfoTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_SEND_MONEY)) {

            try {
                SendMoneyResponse mSendMoneyResponse = gson.fromJson(result.getJsonString(), SendMoneyResponse.class);
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                            mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                        } else {
                            mCustomProgressDialog.showSuccessAnimationAndMessage(mSendMoneyResponse.getMessage());
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCustomProgressDialog.dismissDialog();
                                getActivity().finish();
                            }
                        }, 2000);

                        //Google Analytic event
                        Utilities.sendSuccessEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                        break;
                    case Constants.HTTP_RESPONSE_STATUS_ACCEPTED:
                    case Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED:
                        mCustomProgressDialog.dismissDialog();
                        Toast.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        SecuritySettingsActivity.otpDuration = mSendMoneyResponse.getOtpValidFor();
                        launchOTPVerification();
                        break;
                    case Constants.HTTP_RESPONSE_STATUS_BLOCKED:
                        if (getActivity() != null) {
                            mCustomProgressDialog.showFailureAnimationAndMessage(mSendMoneyResponse.getMessage());
                            ((MyApplication) getActivity().getApplication()).launchLoginPage("");

                            Utilities.sendBlockedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                        }
                        break;
                    default:
                        if (getActivity() != null) {
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                                mCustomProgressDialog.showFailureAnimationAndMessage(mSendMoneyResponse.getMessage());
                            } else {
                                Toast.makeText(mContext, mSendMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            if (mSendMoneyResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
                                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.showOtpDialog();
                                    mCustomProgressDialog.dismissDialog();
                                }
                            } else {
                                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                                }
                            }
                            //Google Analytic event
                            Utilities.sendFailedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(),
                                    mSendMoneyResponse.getMessage(), new BigDecimal(mAmount).longValue());
                            break;
                        }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                }
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                mCustomProgressDialog.showFailureAnimationAndMessage(getResources().getString(R.string.service_not_available));
            }
            mSendMoneyTask = null;
        }
    }


    private void switchActivity(Class tClass) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(Constants.MOBILE_NUMBER, mReceiver);
        intent.putExtra(Constants.FROM_QR_SCAN, true);
        intent.putExtra(Constants.NAME, mName);
        intent.putExtra(Constants.PHOTO_URI, mProfilePicture);
        intent.putExtra(Constants.COUNTRY, country);
        intent.putExtra(Constants.DISTRICT, district);
        intent.putExtra(Constants.ADDRESS, address);
        intent.putExtra(Constants.THANA, thana);
        startActivity(intent);
        getActivity().finish();
    }
}
