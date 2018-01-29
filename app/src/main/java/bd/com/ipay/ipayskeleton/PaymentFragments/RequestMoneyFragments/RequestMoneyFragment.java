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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.CustomContactsSearchView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
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

    private Button buttonRequest;
    private ImageView buttonSelectFromContacts;
    private ImageView buttonScanQRCode;
    private CustomContactsSearchView mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private ProgressDialog mProgressDialog;

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

        mMobileNumberEditText.setCurrentFragmentTag(Constants.REQUEST_MONEY);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumberEditText.setText(getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER));
        }

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

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
                Utilities.performQRCodeScan(RequestMoneyFragment.this, REQUEST_CODE_PERMISSION);

            }
        });

        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs())
                        launchReviewPage();
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
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

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        if (RequestMoneyActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
        }
        // validation check of amount
        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            focusView = mAmountEditText;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            cancel = true;

        } else if ((mAmountEditText.getText().toString().trim().length() > 0)
                && Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                && Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

            BigDecimal maxAmount = RequestMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT();

            String error_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    RequestMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(), maxAmount);

            if (error_message != null) {
                focusView = mAmountEditText;
                mAmountEditText.setError(error_message);
                cancel = true;
            }
        }

        if (!Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
        }

        if (!(mDescriptionEditText.getText().toString().trim().length() > 0)) {
            focusView = mDescriptionEditText;
            mDescriptionEditText.setError(getString(R.string.please_write_note));
            cancel = true;
        }

        if (!InputValidator.isValidNumber(mobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        } else if (ContactEngine.formatMobileNumberBD(mobileNumber).equals(ProfileInfoCacheManager.getMobileNumber())) {
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
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            if (mobileNumber != null) {
                mMobileNumberEditText.setText(mobileNumber);
            }
        } else if (requestCode == REQUEST_MONEY_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            ((RequestMoneyActivity) getActivity()).switchToMoneyRequestListFragment(true);
        } else if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            final String[] resultElements = result.split(" ");

            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (InputValidator.isValidNumber(resultElements[0])) {
                            mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(resultElements[0]));
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
        }
    }

    private void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null) {
            return;
        }

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_VERIFICATION_REQUIRED)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_REQUEST_MONEY_PIN_REQUIRED)) {
                                RequestMoneyActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mGetBusinessRuleTask = null;
        }
    }

}
