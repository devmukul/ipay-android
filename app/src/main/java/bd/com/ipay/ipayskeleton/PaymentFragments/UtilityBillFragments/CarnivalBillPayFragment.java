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
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.CarnivalBillPayRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.CarnivalCustomerInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill.GenericBillPayResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CarnivalBillPayFragment extends BaseFragment implements HttpResponseListener {
    private TextView mNameTextView;
    private EditText mAccountIDEditText;
    private EditText mAmountEditText;
    private TextView mCurrentPackageView;
    private Button mContinueButton;
    private View infoView;
    private View customerIDView;
    private ProgressDialog mProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private String mAmount;
    private String mCustomerID;

    private HttpRequestGetAsyncTask mCarnivalUserInfoGetTask = null;
    private CarnivalCustomerInfoResponse carnivalCustomerInfoResponse;
    private HttpRequestPostAsyncTask mDozeBillPayTask = null;
    private CarnivalBillPayRequest mCarnivalBillPayRequest;
    private HttpRequestGetAsyncTask mGetBusinessRuleTask;
    private GenericBillPayResponse mGenericBillPayResponse;
    private CustomProgressDialog mCustomProgressDialog;

    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doze_bill_pay, container, false);
        if (getActivity() == null)
            return view;
        getActivity().setTitle("Carnival");
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

        mNameTextView = view.findViewById(R.id.name_view);
        customerIDView = view.findViewById(R.id.customer_id_view);
        infoView = view.findViewById(R.id.info_view);
        mCurrentPackageView = view.findViewById(R.id.current_package_view);
        mAccountIDEditText = view.findViewById(R.id.customer_id_edit_text);
        mAmountEditText = view.findViewById(R.id.amount_edit_text);
        mContinueButton = view.findViewById(R.id.continue_button);

        UtilityBillPaymentActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.UTILITY_BILL_PAYMENT);
        setUpButtonAction();
    }

    private void launchOTPVerification() {
        if (getActivity() == null)
            return;
        String jsonString = new Gson().toJson(mCarnivalBillPayRequest);
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
                        if (ifUserEligibleToPaySufficient()) {
                            attemptBillPayWithPinCheck();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean ifUserEligibleToPaySufficient() {
        String errorMessage;
        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());
            //validation check of amount
            if (mAmountEditText.getText() != null) {
                if (mAmountEditText.getText() != null && !TextUtils.isEmpty(mAmountEditText.getText().toString())) {
                    mAmount = mAmountEditText.getText().toString();
                    final BigDecimal topUpAmount = new BigDecimal(mAmountEditText.getText().toString());
                    if (topUpAmount.compareTo(balance) > 0) {
                        errorMessage = getString(R.string.insufficient_balance);
                    } else {
                        final BigDecimal minimumTopupAmount = UtilityBillPaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                        final BigDecimal maximumTopupAmount = UtilityBillPaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                        errorMessage = InputValidator.isValidAmount(getActivity(), topUpAmount, minimumTopupAmount, maximumTopupAmount);
                    }
                } else {
                    errorMessage = getString(R.string.please_enter_amount);
                }
            } else {
                errorMessage = getString(R.string.please_enter_amount);
            }
        } else {
            errorMessage = getString(R.string.balance_not_available);
        }
        if (errorMessage != null) {
            mAmountEditText.setError(errorMessage);
            return false;
        } else {
            return true;
        }
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
        if (mDozeBillPayTask == null) {
            mCarnivalBillPayRequest = new CarnivalBillPayRequest(mCustomerID, mAmount, pin);

            Gson gson = new Gson();
            String json = gson.toJson(mCarnivalBillPayRequest);
            mDozeBillPayTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CARNIVAL_BILL_PAY,
                    Constants.BASE_URL_UTILITY + Constants.URL_CARNIVAL_BILL_PAY, json, getActivity(), false);
            mDozeBillPayTask.mHttpResponseListener = this;
            mDozeBillPayTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mCustomProgressDialog.setLoadingMessage(getString(R.string.please_wait));
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
        if (mCarnivalUserInfoGetTask == null) {
            mProgressDialog.setMessage(getString(R.string.please_wait));
            String mUri = Constants.BASE_URL_UTILITY + Constants.URL_CARNIVAL + mCustomerID;
            mCarnivalUserInfoGetTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DOZE_CUSTOMER, mUri,
                    getActivity(), this, true);
            mCarnivalUserInfoGetTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            mProgressDialog.show();
        }
    }

    private boolean verifyUserInput() {
        Editable editable;
        editable = mAccountIDEditText.getText();
        if (editable == null) {
            mAccountIDEditText.setError(getString(R.string.enter_customer_id));
            return false;
        } else {
            mCustomerID = editable.toString();
            if (TextUtils.isEmpty(mCustomerID)) {
                mAccountIDEditText.setError(getString(R.string.enter_customer_id));
                return false;
            } else {
                return true;
            }
        }
    }

    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

    private void setupView() {
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        mNameTextView.setText(carnivalCustomerInfoResponse.getName());
        mCurrentPackageView.setText(numberFormat.format(new BigDecimal(carnivalCustomerInfoResponse.getCurrentPackageRate())));
        mContinueButton.setText(R.string.pay_bill);
        infoView.setVisibility(View.VISIBLE);
        customerIDView.setVisibility(View.GONE);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() == null)
            return;

        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            mCustomProgressDialog.dismissDialog();
            mCarnivalUserInfoGetTask = null;
            mDozeBillPayTask = null;
        } else {
            try {
                Gson gson = new Gson();
                switch (result.getApiCommand()) {
                    case Constants.COMMAND_GET_DOZE_CUSTOMER:
                        carnivalCustomerInfoResponse = gson.fromJson(result.getJsonString(), CarnivalCustomerInfoResponse.class);
                        if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                            setupView();
                        } else {
                            Toast.makeText(getContext(), carnivalCustomerInfoResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        mCarnivalUserInfoGetTask = null;
                        break;
                    case Constants.COMMAND_GET_BUSINESS_RULE:
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
                        break;
                    case Constants.COMMAND_CARNIVAL_BILL_PAY:
                        try {
                            mGenericBillPayResponse = gson.fromJson(result.getJsonString(), GenericBillPayResponse.class);
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
                                    mCustomProgressDialog.showSuccessAnimationAndMessage(mGenericBillPayResponse.getMessage());
                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getActivity().setResult(Activity.RESULT_OK);
                                        getActivity().finish();

                                    }
                                }, 2000);
                                Utilities.sendSuccessEventTracker(mTracker, Constants.CARNIVAL_BILL_PAY, ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());

                            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                                mCustomProgressDialog.showFailureAnimationAndMessage(mGenericBillPayResponse.getMessage());
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ((MyApplication) getActivity().getApplication()).launchLoginPage(mGenericBillPayResponse.getMessage());
                                    }
                                }, 2000);
                                Utilities.sendBlockedEventTracker(mTracker, Constants.CARNIVAL_BILL_PAY, ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
                                final String errorMessage;
                                if (!TextUtils.isEmpty(mGenericBillPayResponse.getMessage())) {
                                    errorMessage = mGenericBillPayResponse.getMessage();
                                } else {
                                    errorMessage = getString(R.string.recharge_failed);
                                }
                                mCustomProgressDialog.showFailureAnimationAndMessage(errorMessage);
                            } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                                Toast.makeText(getActivity(), mGenericBillPayResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                mCustomProgressDialog.dismissDialog();
                                SecuritySettingsActivity.otpDuration = mGenericBillPayResponse.getOtpValidFor();
                                launchOTPVerification();
                            } else {
                                if (getActivity() != null) {
                                    if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                                        mCustomProgressDialog.showFailureAnimationAndMessage(mGenericBillPayResponse.getMessage());
                                    } else {
                                        Toast.makeText(getContext(), mGenericBillPayResponse.getMessage(), Toast.LENGTH_LONG).show();
                                    }

                                    if (mGenericBillPayResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
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
                                Utilities.sendFailedEventTracker(mTracker, Constants.CARNIVAL_BILL_PAY, ProfileInfoCacheManager.getAccountId(), mGenericBillPayResponse.getMessage(), new BigDecimal(mAmount).longValue());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            mCustomProgressDialog.showFailureAnimationAndMessage(getString(R.string.payment_failed));
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                            }
                        }
                        mDozeBillPayTask = null;
                        break;
                }
            } catch (Exception e) {
                mProgressDialog.dismiss();
                mCarnivalUserInfoGetTask = null;
                mDozeBillPayTask = null;
                mGetBusinessRuleTask = null;
                e.printStackTrace();
                Toast.makeText(getContext(), getString(R.string.request_failed), Toast.LENGTH_LONG).show();
            }
        }

    }
}
