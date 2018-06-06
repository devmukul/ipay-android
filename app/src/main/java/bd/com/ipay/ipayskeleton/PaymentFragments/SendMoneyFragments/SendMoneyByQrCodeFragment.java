package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.QRScanner.BarcodeCaptureActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyByQrCodeFragment extends BaseFragment implements HttpResponseListener {

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private final int PICK_CONTACT_REQUEST = 100;
    private final int SEND_MONEY_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private Button buttonSend;

    private TextView mMobileNumberTextView;
    private TextView mNameTextView;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private ProfileImageView mProfileImageView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_money_by_qr_code, container, false);
        mMobileNumberTextView = (TextView) v.findViewById(R.id.receiver_mobile_number_text_view);
        mNameTextView = (TextView) v.findViewById(R.id.receiver_name_text_view);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mProfileImageView = (ProfileImageView) v.findViewById(R.id.receiver_profile_image_view);
        buttonSend = (Button) v.findViewById(R.id.button_send_money);
        SendMoneyActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.SEND_MONEY);
        try {

            if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
                mMobileNumberTextView.setText(getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER));
            }
            if (getActivity().getIntent().hasExtra(Constants.NAME)) {
                mNameTextView.setText(getActivity().getIntent().getStringExtra(Constants.NAME));
            }
            if (getActivity().getIntent().hasExtra(Constants.PHOTO_URI)) {
                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + getActivity().getIntent().getStringExtra(Constants.PHOTO_URI),
                        false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
            }
        });


        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_SEND_MONEY);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_send_money_by_qr_c));
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

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mDescriptionEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage = null;


        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);
                focusView = mAmountEditText;
                cancel = true;

            } else {
                final BigDecimal sendMoneyAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (sendMoneyAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                }
                if (Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

                    final BigDecimal minimumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), sendMoneyAmount, minimumSendMoneyAmount, maximumSendMoneyAmount);
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
        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void launchReviewPage() {

        String receiver = mMobileNumberTextView.getText().toString().trim();
        BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString().trim());
        String description = mDescriptionEditText.getText().toString().trim();

        Intent intent = new Intent(getActivity(), SendMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.FROM_QR_SCAN, true);
        intent.putExtra(Constants.RECEIVER_MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(receiver));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);
        intent.putExtra(Constants.IS_IN_CONTACTS, new ContactSearchHelper(getActivity()).searchMobileNumber(receiver));

        startActivityForResult(intent, SEND_MONEY_REVIEW_REQUEST);
    }

    private void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null) {
            return;
        }

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this, true);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            return;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            }
                        }
                        BusinessRuleCacheManager.setBusinessRules(Constants.SEND_MONEY, SendMoneyActivity.mMandatoryBusinessRules);
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
        }
    }
}
