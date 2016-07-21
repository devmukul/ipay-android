package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
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

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.FriendPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyFragment extends Fragment implements HttpResponseListener {

    private final int PICK_CONTACT_REQUEST = 100;
    private final int SEND_MONEY_REVIEW_REQUEST = 101;

    private Button buttonSend;
    private ImageView buttonSelectFromContacts;
    private ImageView buttonScanQRCode;
    private EditText mMobileNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_money, container, false);
        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonScanQRCode = (ImageView) v.findViewById(R.id.button_scan_qr_code);
        buttonSelectFromContacts = (ImageView) v.findViewById(R.id.select_receiver_from_contacts);
        buttonSend = (Button) v.findViewById(R.id.button_send_money);

        if (getActivity().getIntent().hasExtra(Constants.MOBILE_NUMBER)) {
            mMobileNumberEditText.setText(getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER));
        }

        buttonSelectFromContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendPickerDialogActivity.class);
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
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        buttonScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_PERMISSION);
                } else initiateScan();

            }
        });

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_SEND_MONEY);

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateScan();
                } else {
                    Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void initiateScan() {
        IntentIntegrator.forFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
            if (mobileNumber != null)
                mMobileNumberEditText.setText(mobileNumber);
                mMobileNumberEditText.setError(null);
        } else if (requestCode == SEND_MONEY_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
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
                        if (ContactEngine.isValidNumber(result)) {
                            mMobileNumberEditText.setText(ContactEngine.formatMobileNumberBD(result));
                        } else if (getActivity() != null)
                            Toast.makeText(getActivity(), getResources().getString(
                                    R.string.please_scan_a_valid_pin), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        //validation check of amount
        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            focusView = mAmountEditText;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            cancel = true;

        } else if ((mAmountEditText.getText().toString().trim().length() > 0)
                && Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                && Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

            String error_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(), SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

            if (error_message != null) {
                focusView = mAmountEditText;
                mAmountEditText.setError(error_message);
                cancel = true;
            }
        }

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
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

        Intent intent = new Intent(getActivity(), SendMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(Constants.INVOICE_RECEIVER_TAG, ContactEngine.formatMobileNumberBD(receiver));
        intent.putExtra(Constants.INVOICE_DESCRIPTION_TAG, description);

        startActivityForResult(intent, SEND_MONEY_REVIEW_REQUEST);

    }

    protected void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null) {
            return;
        }

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(Constants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());

                            } else if (rule.getRuleID().equals(Constants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
            }

            mGetBusinessRuleTask = null;
        }
    }

}
