package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.CustomContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyFragment extends BaseFragment implements HttpResponseListener {
    public static final int REQUEST_CODE_PERMISSION = 1001;

    private final int PICK_CONTACT_REQUEST = 100;
    private final int REQUEST_MONEY_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;
    private HttpRequestGetAsyncTask mGetUserInfoTask;
    private HttpRequestPostAsyncTask mRequestMoneyTask = null;

    private Button buttonRequest;
    private ImageView buttonSelectFromContacts;
    private ImageView buttonScanQRCode;
    private CustomContactsSearchView mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private View mProfilePicHolderView;
    private View mMobileNumberHolderView;
    private View mIconEditMobileNumber;
    private ProfileImageView mProfileImageView;
    private TextView mNameTextView;
    private ProgressDialog mProgressDialog;
    private CheckBox addToContactCheckBox;
    private String mMobileNumber;
    private String mAmount;
    private String mDescription;
    private String mName;
    private String mProfilePicture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request_money, container, false);
        mMobileNumberEditText = (CustomContactsSearchView) v.findViewById(R.id.mobile_number);
        buttonScanQRCode = (ImageView) v.findViewById(R.id.button_scan_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_sender_from_contacts);
        buttonRequest = (Button) v.findViewById(R.id.button_request_money);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mProfileImageView = (ProfileImageView) v.findViewById(R.id.receiver_profile_image_view);
        mProfilePicHolderView = v.findViewById(R.id.profile_pic_holder);
        mMobileNumberHolderView = v.findViewById(R.id.mobile_number_holder);
        mIconEditMobileNumber = v.findViewById(R.id.edit_icon_mobile_number);
        mNameTextView = (TextView) v.findViewById(R.id.receiver_name_text_view);
        addToContactCheckBox = (CheckBox) v.findViewById(R.id.add_to_contact_check_box);
        mProgressDialog = new ProgressDialog(getActivity());

        mMobileNumberEditText.setCurrentFragmentTag(Constants.REQUEST_MONEY);

        RequestMoneyActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.REQUEST_MONEY);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumber = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);
            mMobileNumberEditText.setText(mMobileNumber);
            getUserInfo(ContactEngine.formatMobileNumberBD(mMobileNumber));
        }


        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                intent.putExtra(Constants.IPAY_MEMBERS_ONLY, true);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });

        buttonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
                startActivityForResult(intent, Constants.RC_BARCODE_CAPTURE);
            }
        });

        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs())
                        attemptRequestMoney();
                    if (addToContactCheckBox.isChecked()) {
                        addContact(mName, mMobileNumber, null);
                    }
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
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

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_REQUEST_MONEY);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_request_money));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_request_money_history, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Remove search action of contacts
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                ((RequestMoneyActivity) getActivity()).switchToMoneyRequestListFragment(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage = null;

        mMobileNumber = mMobileNumberEditText.getText().toString().trim();
        mAmount = mAmountEditText.getText().toString().trim();
        mDescription = mDescriptionEditText.getText().toString().trim();

        if (!Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        }

        if (RequestMoneyActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        // validation check of amount
        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            errorMessage = getString(R.string.please_enter_amount);
        } else if (!InputValidator.isValidDigit(mAmountEditText.getText().toString().trim())) {
            errorMessage = getString(R.string.please_enter_amount);
        } else if (mAmountEditText.getText().toString().trim().length() > 0 && InputValidator.isValidDigit(mAmountEditText.getText().toString().trim())) {

            BigDecimal maxAmount = RequestMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT();

            errorMessage = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    RequestMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(), maxAmount);
        }

        if (errorMessage != null) {
            focusView = mAmountEditText;
            mAmountEditText.setError(errorMessage);
            cancel = true;
        }

        if (!(mDescriptionEditText.getText().toString().trim().length() > 0)) {
            focusView = mDescriptionEditText;
            mDescriptionEditText.setError(getString(R.string.please_write_note));
            cancel = true;
        }

        if (!InputValidator.isValidNumber(mMobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        } else if (ContactEngine.formatMobileNumberBD(mMobileNumber).equals(ProfileInfoCacheManager.getMobileNumber())) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.you_cannot_request_money_from_your_number));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void launchReviewPage() {
        String receiver = mMobileNumberEditText.getText().toString().trim();
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String description = mDescriptionEditText.getText().toString().trim();

        Intent intent = new Intent(getActivity(), RequestMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(receiver));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);
        intent.putExtra(Constants.IS_IN_CONTACTS, new ContactSearchHelper(getActivity()).searchMobileNumber(receiver));

        startActivityForResult(intent, REQUEST_MONEY_REVIEW_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    final String result = barcode.displayValue;
                    final String[] resultElements = result.split(" ");

                    if (result != null) {
                        Handler mHandler = new Handler();
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (InputValidator.isValidNumber(resultElements[0])) {
                                    mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(resultElements[0]));
                                    if (Utilities.isConnectionAvailable(getActivity())) {
                                        String mobileNumber = ContactEngine.formatMobileNumberBD(resultElements[0]);
                                        getUserInfo(mobileNumber);
                                    }
                                    if (resultElements.length > 1) {
                                        switch (resultElements.length) {
                                            case 2: {
                                                mAmountEditText.setText(resultElements[1]);
                                                break;
                                            }
                                            case 3: {
                                                mAmountEditText.setText(resultElements[1]);
                                                mDescriptionEditText.setText(resultElements[2]);
                                                break;
                                            }
                                        }
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
        } else if (requestCode == REQUEST_MONEY_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            getActivity().finish();
        }
    }

    private void getUserInfo(String mobileNumber) {
        mMobileNumber = mobileNumber;
        GetUserInfoRequestBuilder getUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        if (mGetUserInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.please_wait_loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mGetUserInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                getUserInfoRequestBuilder.getGeneratedUri(), getActivity(), false);
        mGetUserInfoTask.mHttpResponseListener = RequestMoneyFragment.this;
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
                mUri, getActivity(), this, true);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptRequestMoney() {
        if (mRequestMoneyTask != null) {
            return;
        }

        Utilities.hideKeyboard(getActivity());
        mProgressDialog.setMessage(getString(R.string.requesting_money));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        RequestMoneyRequest mRequestMoneyRequest = new RequestMoneyRequest(ContactEngine.formatMobileNumberBD(mMobileNumber),
                new BigDecimal(mAmount).doubleValue(), mDescription);
        Gson gson = new Gson();
        String json = gson.toJson(mRequestMoneyRequest);
        mRequestMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REQUEST_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY, json, getActivity(), false);
        mRequestMoneyTask.mHttpResponseListener = this;
        mRequestMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetUserInfoTask = null;
            mRequestMoneyTask = null;
            mGetBusinessRuleTask = null;
            return;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_VERIFICATION_REQUIRED)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_PIN_REQUIRED)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            }
                        }

                        BusinessRuleCacheManager.setBusinessRules(Constants.REQUEST_MONEY, RequestMoneyActivity.mMandatoryBusinessRules);
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
        }
        if (result.getApiCommand().equals(Constants.COMMAND_REQUEST_MONEY)) {

            try {
                RequestMoneyResponse mRequestMoneyResponse = gson.fromJson(result.getJsonString(), RequestMoneyResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();

                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_LONG);

                    //Google Analytic event
                    Utilities.sendSuccessEventTracker(mTracker, "Request Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_SHORT);

                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Request Money", ProfileInfoCacheManager.getAccountId(), mRequestMoneyResponse.getMessage(), new BigDecimal(mAmount).longValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.failed_request_money, Toast.LENGTH_SHORT);
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }

            mProgressDialog.dismiss();
            mRequestMoneyTask = null;

        }
    }

}
