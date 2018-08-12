package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
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
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRuleV2;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.Rule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.DescoBillPayRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.DescoBillPayResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.DescoCustomerInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class DescoBillPaymentFragment extends BaseFragment implements HttpResponseListener {
    private TextView mAccountIDTextView;
    private TextView mDueDateTextView;
    private TextView mNetAmountTextView;
    private TextView mVatTextView;
    private TextView mTotalAmountTextView;
    private TextView mIpcTextView;
    private TextView mBillStatusTextView;
    private TextView mBillNumberTextView;
    private EditText mEnterBillNumberEditText;
    private Button mContinueButton;
    private View infoView;
    private View customerIDView;
    private ProgressDialog mProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private String mUri;
    private String mBillNumber;
    private String mAmount;

    private HttpRequestGetAsyncTask mDescoCustomerInfoTask = null;
    private DescoCustomerInfoResponse mDescoCustomerInfoResponse;
    private HttpRequestPostAsyncTask mDescoBillPayTask = null;
    private DescoBillPayRequest mDescoBillPayRequest;
    private HttpRequestGetAsyncTask mGetBusinessRuleTask;
    private DescoBillPayResponse mDescoBillPayResponse;
    private CustomProgressDialog mCustomProgressDialog;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_desco_bill_payment, container, false);
        getActivity().setTitle("Desco");
        attemptGetBusinessRule(ServiceIdConstants.UTILITY_BILL_PAYMENT);
        mProgressDialog = new ProgressDialog(getContext());
        mCustomProgressDialog = new CustomProgressDialog(getContext());
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        if (UtilityBillPaymentActivity.mMandatoryBusinessRules == null) {
            UtilityBillPaymentActivity.mMandatoryBusinessRules = new MandatoryBusinessRules(Constants.UTILITY_BILL_PAYMENT);
        }

        mAccountIDTextView = (TextView) view.findViewById(R.id.account_number_view);
        mNetAmountTextView = (TextView) view.findViewById(R.id.net_amount_view);
        mVatTextView = (TextView) view.findViewById(R.id.vat_amount_view);
        mTotalAmountTextView = (TextView) view.findViewById(R.id.total_amount_view);
        mIpcTextView = (TextView) view.findViewById(R.id.ipc_view);
        mBillStatusTextView = (TextView) view.findViewById(R.id.bill_status_view);
        mBillNumberTextView = (TextView) view.findViewById(R.id.bill_number_view);
        mDueDateTextView = (TextView) view.findViewById(R.id.due_date_view);
        customerIDView = view.findViewById(R.id.customer_id_view);
        infoView = view.findViewById(R.id.info_view);
        mEnterBillNumberEditText = (EditText) view.findViewById(R.id.customer_id_edit_text);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);
        UtilityBillPaymentActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.UTILITY_BILL_PAYMENT);
        setUpButtonAction();
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mDescoBillPayRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_BANGLALION_BILL_PAY,
                Constants.BASE_URL_UTILITY + Constants.URL_BANGLALION_BILL_PAY, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;

    }

    private void setUpButtonAction() {
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utilities.isConnectionAvailable(getContext())) {
                    if (mContinueButton.getText().toString().toUpperCase().equals("CONTINUE")) {
                        if (verifyUserInput()) {
                            getCustomerInfo();
                        }
                    } else {
                        attemptBillPayWithPinCheck();
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void attemptBillPayWithPinCheck() {
        if (UtilityBillPaymentActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptBillPay(pin);
                }
            });
        } else {
            attemptBillPay(null);
        }
    }

    private void attemptBillPay(String pin) {
        if (mDescoBillPayTask != null) {
            return;
        } else {
            mDescoBillPayRequest = new DescoBillPayRequest(mBillNumber, pin);

            Gson gson = new Gson();
            String json = gson.toJson(mDescoBillPayRequest);
            mDescoBillPayTask = new HttpRequestPostAsyncTask(Constants.COMMAND_WEST_ZONE_BILL_PAY,
                    Constants.BASE_URL_UTILITY + Constants.URL_DESCO_BILL_PAY, json, getActivity(), false);
            mDescoBillPayTask.mHttpResponseListener = this;
            mDescoBillPayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mCustomProgressDialog.setLoadingMessage("Please wait");
            mCustomProgressDialog.showDialog();
        }
    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null) {
            return;
        }
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                Constants.BASE_URL_SM + Constants.URL_BUSINESS_RULE_V2 + "/" + serviceID, getActivity(), false);
        mGetBusinessRuleTask.mHttpResponseListener = this;
        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getCustomerInfo() {
        if (mDescoCustomerInfoTask != null) {
            return;
        } else {
            mProgressDialog.setMessage("Please wait");
            mUri = Constants.BASE_URL_UTILITY + Constants.URL_DESCO_CUSTOMER_INFO + mBillNumber;
            mDescoCustomerInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DESCO_CUSTOMER, mUri,
                    getActivity(), this, true);
            mDescoCustomerInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mProgressDialog.show();
        }
    }

    private boolean verifyUserInput() {
        Editable editable;
        editable = mEnterBillNumberEditText.getText();
        if (editable == null) {
            mEnterBillNumberEditText.setError(getString(R.string.enter_customer_id));
            return false;
        } else {
            mBillNumber = editable.toString();
            if (mBillNumber == null || mBillNumber.isEmpty()) {
                mEnterBillNumberEditText.setError(getString(R.string.enter_bill_no));
                return false;
            } else {
                return true;
            }
        }
    }

    private void fillUpFiledsWithData() {
        mNetAmountTextView.setText(mDescoCustomerInfoResponse.getNetAmount());
        mBillStatusTextView.setText(mDescoCustomerInfoResponse.getBillStatus());
        mAccountIDTextView.setText(mDescoCustomerInfoResponse.getAccountNumber());
        mVatTextView.setText(mDescoCustomerInfoResponse.getVatAmount());
        mTotalAmountTextView.setText(mDescoCustomerInfoResponse.getTotalPayableAmount());
        mAmount = mDescoCustomerInfoResponse.getTotalPayableAmount();
        mBillNumberTextView.setText(mBillNumber);
        mIpcTextView.setText(mDescoCustomerInfoResponse.getLpc());
        mContinueButton.setText("Pay bill");
        mDueDateTextView.setText(mDescoCustomerInfoResponse.getDueDate());
        infoView.setVisibility(View.VISIBLE);
        customerIDView.setVisibility(View.GONE);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            mCustomProgressDialog.dismissDialog();
            mDescoCustomerInfoTask = null;
            mDescoBillPayTask = null;
            return;
        } else {
            try {
                Gson gson = new Gson();
                if (result.getApiCommand().equals(Constants.COMMAND_GET_DESCO_CUSTOMER)) {
                    mDescoCustomerInfoResponse = gson.fromJson(result.getJsonString(), DescoCustomerInfoResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        fillUpFiledsWithData();
                    } else {
                        Toast.makeText(getContext(), mDescoCustomerInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    mDescoCustomerInfoTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        gson = new Gson();

                        BusinessRuleV2 businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRuleV2.class);
                        List<Rule> rules = businessRuleArray.getRules();

                        for (Rule rule : rules) {
                            switch (rule.getRuleName()) {
                                case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_MAX_AMOUNT_PER_PAYMENT:
                                    UtilityBillPaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                    break;
                                case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_MIN_AMOUNT_PER_PAYMENT:
                                    UtilityBillPaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                    break;
                                case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_VERIFICATION_REQUIRED:
                                    UtilityBillPaymentActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                    break;
                                case BusinessRuleConstants.SERVICE_RULE_UTILITY_BILL_PAYMENT_PIN_REQUIRED:
                                    UtilityBillPaymentActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                                    break;
                            }

                            BusinessRuleCacheManager.setBusinessRules(Constants.UTILITY_BILL_PAYMENT, UtilityBillPaymentActivity.mMandatoryBusinessRules);
                        }
                    }
                    mGetBusinessRuleTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_WEST_ZONE_BILL_PAY)) {
                    try {
                        mDescoBillPayResponse = gson.fromJson(result.getJsonString(), DescoBillPayResponse.class);
                        if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                            if (getActivity() != null) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getActivity().setResult(Activity.RESULT_OK);
                                        getActivity().finish();
                                    }
                                }, 2000);
                            }
                        } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                            if (mContinueButton != null) {
                                mContinueButton.setClickable(false);
                            }

                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                            } else {
                                mCustomProgressDialog.showSuccessAnimationAndMessage(mDescoBillPayResponse.getMessage());
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();

                                }
                            }, 2000);
                            Utilities.sendSuccessEventTracker(mTracker, Constants.DESCO_BILL_PAY, ProfileInfoCacheManager.getAccountId(),new BigDecimal(mAmount).longValue());

                        } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                            mCustomProgressDialog.showFailureAnimationAndMessage(mDescoBillPayResponse.getMessage());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((MyApplication) getActivity().getApplication()).launchLoginPage(mDescoBillPayResponse.getMessage());
                                }
                            }, 2000);
                            Utilities.sendBlockedEventTracker(mTracker, Constants.DESCO_BILL_PAY, ProfileInfoCacheManager.getAccountId());
                        } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
                            final String errorMessage;
                            if (!TextUtils.isEmpty(mDescoBillPayResponse.getMessage())) {
                                errorMessage = mDescoBillPayResponse.getMessage();
                            } else {
                                errorMessage = getString(R.string.payment_failed);
                            }
                            mCustomProgressDialog.showFailureAnimationAndMessage(errorMessage);
                        } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                            Toast.makeText(getActivity(), mDescoBillPayResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            mCustomProgressDialog.dismissDialog();
                            SecuritySettingsActivity.otpDuration = mDescoBillPayResponse.getOtpValidFor();
                            launchOTPVerification();
                        } else {
                            if (getActivity() != null) {
                                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                                    mCustomProgressDialog.showFailureAnimationAndMessage(mDescoBillPayResponse.getMessage());
                                } else {
                                    Toast.makeText(getContext(), mDescoBillPayResponse.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                if (mDescoBillPayResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
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
                            }
                            Utilities.sendFailedEventTracker(mTracker, Constants.DESCO_BILL_PAY, ProfileInfoCacheManager.getAccountId(), mDescoBillPayResponse.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mCustomProgressDialog.showFailureAnimationAndMessage(getString(R.string.recharge_failed));
                        if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                            mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                        }
                    }
                    mDescoBillPayTask = null;
                }
            } catch (Exception e) {
                mProgressDialog.dismiss();
                mDescoCustomerInfoTask = null;
                mDescoBillPayTask = null;
                mGetBusinessRuleTask = null;
                e.printStackTrace();
            }
        }

    }
}

