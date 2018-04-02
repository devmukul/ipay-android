package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpReviewActivity;
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
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupFragment extends BaseFragment implements HttpResponseListener {

    private static final int MOBILE_TOPUP_REVIEW_REQUEST = 101;
    private final int PICK_CONTACT_REQUEST = 100;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;
    private HttpRequestGetAsyncTask mGetUserInfoTask;
    private HttpRequestPostAsyncTask mTopupTask = null;
    private TopupResponse mTopupResponse;
    private TopupRequest mTopupRequestModel;

    private CustomContactsSearchView mMobileNumberEditText;
    private EditText mAmountEditText;
    private EditText mPackageEditText;
    private EditText mOperatorEditText;
    private ImageView mSelectReceiverButton;
    private Button mRechargeButton;
    private ProgressDialog mProgressDialog;
    private View mProfilePicHolderView;
    private View mMobileNumberHolderView;
    private View mIconEditMobileNumber;
    private ProfileImageView mProfileImageView;
    private TextView mNameTextView;
    private CheckBox addToContactCheckBox;

    private List<String> mPackageList;
    private List<String> mOperatorList;
    private CustomSelectorDialog mPackageSelectorDialog;
    private CustomSelectorDialogWithIcon mOperatorSelectorDialog;
    private int mSelectedPackageTypeId = -1;
    private int mSelectedOperatorTypeId = 0;
    private String mUserMobileNumber;
    private String mMobileNumber;
    private double mAmount;
    private String mName;
    private String mProfilePicture;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mobile_topup, container, false);

        mMobileNumberEditText = (CustomContactsSearchView) view.findViewById(R.id.mobile_number);
        mAmountEditText = (EditText) view.findViewById(R.id.amount);
        mPackageEditText = (EditText) view.findViewById(R.id.package_type);
        mOperatorEditText = (EditText) view.findViewById(R.id.operator);
        mSelectReceiverButton = (ImageView) view.findViewById(R.id.select_receiver_from_contacts);
        mRechargeButton = (Button) view.findViewById(R.id.button_recharge);
        mProfilePicHolderView = view.findViewById(R.id.profile_pic_holder);
        mMobileNumberHolderView = view.findViewById(R.id.mobile_number_holder);
        mIconEditMobileNumber = view.findViewById(R.id.edit_icon_mobile_number);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.receiver_profile_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.receiver_name_text_view);
        addToContactCheckBox = (CheckBox) view.findViewById(R.id.add_to_contact_check_box);

        mProgressDialog = new ProgressDialog(getActivity());

        mUserMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        setOperatorAndPackageAdapter();

        int mobileNumberType = SharedPrefManager.getMobileNumberType(Constants.MOBILE_TYPE_PREPAID);
        if (mobileNumberType == Constants.MOBILE_TYPE_PREPAID) {
            mPackageEditText.setText(mPackageList.get(Constants.MOBILE_TYPE_PREPAID - 1));
            mSelectedPackageTypeId = Constants.MOBILE_TYPE_PREPAID - 1;
        } else {
            mPackageEditText.setText(mPackageList.get(Constants.MOBILE_TYPE_POSTPAID - 1));
            mSelectedPackageTypeId = Constants.MOBILE_TYPE_POSTPAID - 1;
        }

        mPackageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPackageSelectorDialog.show();
            }
        });

        mOperatorEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setMobileNumber();
        setOperator(mUserMobileNumber);

        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        attemptTopUpWithPinCheck();

                        if (addToContactCheckBox.isChecked()) {
                            addContact(mName, mMobileNumber, null);
                        }
                    }
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
            }
        });

        if (!ProfileInfoCacheManager.isAccountVerified()) {
            mMobileNumberEditText.setEnabledStatus(false);
            mMobileNumberEditText.setFocusableStatus(false);

            mOperatorEditText.setEnabled(false);
            mSelectReceiverButton.setVisibility(View.GONE);
            mAmountEditText.requestFocus();

        } else {
            mMobileNumberEditText.setEnabledStatus(true);
            mMobileNumberEditText.setFocusableStatus(true);
            mSelectReceiverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @ValidateAccess(ServiceIdConstants.GET_CONTACTS)
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            });

            mMobileNumberEditText.requestFocus();
        }

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

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_TOP_UP);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_mobile_topup));
    }

    private void setMobileNumber() {
        mMobileNumberEditText.setCurrentFragmentTag(Constants.TOP_UP);
        mMobileNumberEditText.setCustomTextChangeListener(new ContactsSearchView.CustomTextChangeListener() {
            @Override
            public void onTextChange(String inputText) {
                setOperator(inputText);
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

        mMobileNumberEditText.setText(mUserMobileNumber);
    }

    private void setOperatorAndPackageAdapter() {

        int[] mIconList = getOperatorIcons();

        mPackageList = Arrays.asList(getResources().getStringArray(R.array.package_type));
        mPackageSelectorDialog = new CustomSelectorDialog(getActivity(), getString(R.string.select_a_package), mPackageList);
        mPackageSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int selectedIndex, String mPackage) {
                mPackageEditText.setText(mPackage);
                mSelectedPackageTypeId = mPackageList.indexOf(mPackage);
            }
        });

        mOperatorList = Arrays.asList(getResources().getStringArray(R.array.mobile_operators));
        mOperatorSelectorDialog = new CustomSelectorDialogWithIcon(getActivity(), getString(R.string.select_an_operator), mOperatorList, mIconList);
        mOperatorSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String mOperator) {
                mOperatorEditText.setText(mOperator);
                mSelectedOperatorTypeId = mOperatorList.indexOf(mOperator);
            }
        });
    }

    private void setOperator(String phoneNumber) {
        phoneNumber = phoneNumber.trim();
        final String[] OPERATOR_PREFIXES = getResources().getStringArray(R.array.operator_prefix);
        for (int i = 0; i < OPERATOR_PREFIXES.length; i++) {
            if (phoneNumber.startsWith("+880" + OPERATOR_PREFIXES[i]) ||
                    phoneNumber.startsWith("0" + OPERATOR_PREFIXES[i]) ||
                    phoneNumber.startsWith("880" + OPERATOR_PREFIXES[i]) ||
                    phoneNumber.startsWith(OPERATOR_PREFIXES[i])) {
                mOperatorEditText.setText(mOperatorList.get(i));
                mSelectedOperatorTypeId = i;
                break;
            } else {
                mOperatorEditText.setText(getString(R.string.invalid_operator));
            }
        }
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage;

        if (!Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        }

        if (TopUpActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            //validation check of amount
            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else {
                final BigDecimal topUpAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (topUpAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                } else {
                    final BigDecimal minimumTopupAmount = TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumTopupAmount = TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), topUpAmount, minimumTopupAmount, maximumTopupAmount);
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

        mMobileNumber = mMobileNumberEditText.getText().toString().trim();

        if (!mMobileNumber.matches(InputValidator.MOBILE_NUMBER_REGEX)) {
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            focusView = mMobileNumberEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
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
            }
        } else if (requestCode == MOBILE_TOPUP_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    private void launchReviewPage() {

        // TODO remove this once gateway problem is fixed. We are doing this now because topup
        // gateway only accepts integer amount
        double amount = Math.floor(Double.parseDouble(mAmountEditText.getText().toString().trim()));
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        int mobileNumberType;
        if (mSelectedPackageTypeId > 0)
            mobileNumberType = Constants.MOBILE_TYPE_POSTPAID;
        else
            mobileNumberType = Constants.MOBILE_TYPE_PREPAID;
        SharedPrefManager.setMobileNumberType(mobileNumberType);

        int operatorCode = mSelectedOperatorTypeId + 1;
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        Intent intent = new Intent(getActivity(), TopUpReviewActivity.class);
        intent.putExtra(Constants.MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(mobileNumber));
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(SharedPrefConstants.MOBILE_NUMBER_TYPE, mobileNumberType);
        intent.putExtra(Constants.OPERATOR_CODE, operatorCode);
        intent.putExtra(Constants.COUNTRY_CODE, countryCode);

        startActivityForResult(intent, MOBILE_TOPUP_REVIEW_REQUEST);
    }

    private int[] getOperatorIcons() {
        //Setting the correct image based on Operator
        return new int[]{
                R.drawable.gp,
                R.drawable.gp,
                R.drawable.robi,
                R.drawable.airtel,
                R.drawable.banglalink,
                R.drawable.teletalk,
        };

    }

    private void getUserInfo(String mobileNumber) {
        mMobileNumber = mobileNumber;
        GetUserInfoRequestBuilder getUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        if (mGetUserInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mGetUserInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                getUserInfoRequestBuilder.getGeneratedUri(), getActivity());
        mGetUserInfoTask.mHttpResponseListener = MobileTopupFragment.this;
        mGetUserInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching));
        mProgressDialog.show();

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptTopUpWithPinCheck() {
        if (TopUpActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptTopUp(pin);
                }
            });
        } else {
            attemptTopUp(null);
        }

    }

    private void attemptTopUp(String pin) {
        mAmount = Math.floor(Double.parseDouble(mAmountEditText.getText().toString().trim()));

        int mobileNumberType;
        if (mSelectedPackageTypeId > 0)
            mobileNumberType = Constants.MOBILE_TYPE_POSTPAID;
        else
            mobileNumberType = Constants.MOBILE_TYPE_PREPAID;
        SharedPrefManager.setMobileNumberType(mobileNumberType);

        int operatorCode = mSelectedOperatorTypeId + 1;
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        if (mTopupTask != null)
            return;
        mTopupRequestModel = new TopupRequest(Long.parseLong(mMobileNumber.replaceAll("[^0-9]", "")),
                mMobileNumber, mobileNumberType, operatorCode, mAmount,
                countryCode, mobileNumberType, Constants.DEFAULT_USER_CLASS, pin);

        mProgressDialog.setMessage(getString(R.string.dialog_requesting_top_up));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        Gson gson = new Gson();
        String json = gson.toJson(mTopupRequestModel);
        mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
        mTopupTask.mHttpResponseListener = this;
        mTopupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mTopupRequestModel);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
    }

    @ValidateAccess
    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        Gson gson = new Gson();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    for (BusinessRule rule : businessRuleArray) {
                        if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_MAX_AMOUNT_PER_PAYMENT)) {
                            TopUpActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                        } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_MIN_AMOUNT_PER_PAYMENT)) {
                            TopUpActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                        } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_VERIFICATION_REQUIRED)) {
                            TopUpActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                        } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_TOP_UP_PIN_REQUIRED)) {
                            TopUpActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
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
            try {
                GetUserInfoResponse mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mProfilePicHolderView.setVisibility(View.VISIBLE);
                    mMobileNumberHolderView.setVisibility(View.GONE);

                    if (!new ContactSearchHelper(getActivity()).searchMobileNumber(mMobileNumber)) {
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
            mProgressDialog.dismiss();
        } else if (result.getApiCommand().equals(Constants.COMMAND_TOPUP_REQUEST)) {
            try {
                mTopupResponse = gson.fromJson(result.getJsonString(), TopupResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG);
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();

                        //Google Analytic event
                        Utilities.sendSuccessEventTracker(mTracker, "TopUp Processing", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                    }
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG);
                        getActivity().setResult(Activity.RESULT_OK);
                        if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                            mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                        }
                        getActivity().finish();

                        //Google Analytic event
                        Utilities.sendSuccessEventTracker(mTracker, "TopUp", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                    }
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                    if (getActivity() != null)
                        ((MyApplication) getActivity().getApplication()).launchLoginPage(mTopupResponse.getMessage());
                    Utilities.sendBlockedEventTracker(mTracker, "Topup", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
                    final String errorMessage;
                    if (!TextUtils.isEmpty(mTopupResponse.getMessage())) {
                        errorMessage = mTopupResponse.getMessage();
                    } else {
                        errorMessage = getString(R.string.recharge_failed);
                    }
                    Toaster.makeText(getActivity(), mTopupResponse.getMessage(), Toast.LENGTH_LONG);
                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "TopUp", ProfileInfoCacheManager.getAccountId(), errorMessage, Double.valueOf(mAmount).longValue());
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                    Toast.makeText(getActivity(), mTopupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    SecuritySettingsActivity.otpDuration = mTopupResponse.getOtpValidFor();
                    launchOTPVerification();
                } else {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), mTopupResponse.getMessage(), Toast.LENGTH_LONG);
                    }
                    if (mTopupResponse.getMessage().toLowerCase().contains("wrong")) {
                        mOTPVerificationForTwoFactorAuthenticationServicesDialog.showOtpDialog();
                    } else if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                        mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                    }

                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "TopUp", ProfileInfoCacheManager.getAccountId(), getString(R.string.recharge_failed), Double.valueOf(mAmount).longValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toaster.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG);
                }
                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                }
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }

            mTopupTask = null;
        }
    }
}