package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class WithdrawMoneyReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mWithdrawMoneyTask = null;
    private WithdrawMoneyResponse mWithdrawMoneyResponse;

    private ProgressDialog mProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private double mAmount;
    private String mDescription;
    private long mBankAccountId;
    private String mBankName;
    private String mBankAccountNumber;
    private String mError_message;
    private int mBankCode;

    private WithdrawMoneyRequest mWithdrawMoneyRequest;

    private LinearLayout mLinearLayoutDescriptionHolder;
    private TextView mBankNameView;
    private TextView mBankAccountNumberView;
    private TextView mDescriptionView;
    private View mDescriptionHolder;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private Button mWithdrawMoneyButton;
    private ImageView mBankIcon;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_withdraw_money_review));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_withdraw_money_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);
        mBankAccountId = getActivity().getIntent().getLongExtra(Constants.BANK_ACCOUNT_ID, -1);
        mBankName = getActivity().getIntent().getStringExtra(Constants.BANK_NAME);
        mBankAccountNumber = getActivity().getIntent().getStringExtra(Constants.BANK_ACCOUNT_NUMBER);
        mBankCode = getActivity().getIntent().getIntExtra(Constants.BANK_CODE, 0);
        Drawable bankIcon = getResources().getDrawable(mBankCode);

        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mLinearLayoutDescriptionHolder = (LinearLayout) v.findViewById(R.id.layout_description_holder);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mDescriptionHolder = v.findViewById(R.id.description_holder);
        mBankNameView = (TextView) v.findViewById(R.id.textview_bank_name);
        mBankAccountNumberView = (TextView) v.findViewById(R.id.textview_account_number);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mWithdrawMoneyButton = (Button) v.findViewById(R.id.button_withdraw_money);
        mBankIcon = (ImageView) v.findViewById(R.id.portrait);

        mProgressDialog = new ProgressDialog(getActivity());

        mBankIcon.setImageDrawable(bankIcon);
        mBankNameView.setText(mBankName);
        mBankAccountNumberView.setText(mBankAccountNumber);
        mAmountView.setText(Utilities.formatTaka(mAmount));

        if (mDescription == null || mDescription.isEmpty()) {
            mLinearLayoutDescriptionHolder.setVisibility(View.GONE);
        } else {
            mDescriptionView.setText(mDescription);
        }

        mWithdrawMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    mError_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmount),
                            WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (mError_message == null) {
                        attemptWithdrawMoneyWithPinCheck();

                    } else {
                        showErrorDialog();
                    }
                } else
                    attemptWithdrawMoneyWithPinCheck();
                ;

            }
        });

        // Check if Min or max amount is available
        if (!Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRuleWithServiceCharge(Constants.SERVICE_ID_WITHDRAW_MONEY);
        else
            attemptGetServiceCharge();

        return v;
    }

    private void attemptWithdrawMoneyWithPinCheck() {
        if (WithdrawMoneyActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptWithdrawMoney(pin);
                }
            });
        } else {
            attemptWithdrawMoney(null);
        }
    }

    private void attemptWithdrawMoney(String pin) {
        if (mWithdrawMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_withdraw_money_in_progress));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mWithdrawMoneyRequest = new WithdrawMoneyRequest(mBankAccountId, mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mWithdrawMoneyRequest);
        mWithdrawMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_WITHDRAW_MONEY,
                Constants.BASE_URL_SM + Constants.URL_WITHDRAW_MONEY, json, getActivity());
        mWithdrawMoneyTask.mHttpResponseListener = this;

        mWithdrawMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(mError_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_WITHDRAW_MONEY;
    }

    @Override
    public BigDecimal getAmount() {
        return new BigDecimal(mAmount);
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mTotalView.setText(Utilities.formatTaka(getAmount().subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        WithdrawMoneyActivity.mMandatoryBusinessRules.setIS_PIN_REQUIRED(isPinRequired);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mWithdrawMoneyRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_WITHDRAW_MONEY,
                Constants.BASE_URL_SM + Constants.URL_WITHDRAW_MONEY,Constants.METHOD_POST);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mWithdrawMoneyTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_WITHDRAW_MONEY)) {

            try {
                mWithdrawMoneyResponse = gson.fromJson(result.getJsonString(), WithdrawMoneyResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mWithdrawMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();

                    //Google Analytic event
                    Utilities.sendSuccessEventTracker(mTracker, "Withdraw Money", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                    if (getActivity() != null)
                        ((MyApplication) getActivity().getApplication()).launchLoginPage(mWithdrawMoneyResponse.getMessage());
                    Utilities.sendBlockedEventTracker(mTracker, "Withdraw Money", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());


                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                    Toast.makeText(getActivity(), mWithdrawMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    SecuritySettingsActivity.otpDuration = mWithdrawMoneyResponse.getOtpValidFor();
                    launchOTPVerification();

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                    Toast.makeText(getActivity(), mWithdrawMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    SecuritySettingsActivity.otpDuration = mWithdrawMoneyResponse.getOtpValidFor();
                    launchOTPVerification();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mWithdrawMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();

                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Withdraw Money", ProfileInfoCacheManager.getAccountId(), mWithdrawMoneyResponse.getMessage(), Double.valueOf(mAmount).longValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }

            mProgressDialog.dismiss();
            mWithdrawMoneyTask = null;

        }
    }
}