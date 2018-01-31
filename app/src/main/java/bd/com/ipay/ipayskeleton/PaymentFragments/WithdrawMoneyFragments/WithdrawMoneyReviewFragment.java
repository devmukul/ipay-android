package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
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
    private WithdrawMoneyRequest mWithdrawMoneyRequest;

    private ProgressDialog mProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private double mAmount;
    private String mDescription;
    private UserBankClass mSelectedBank;

    private TextView mServiceChargeTextView;
    private TextView mNetAmountTextView;

    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);

        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);
        mSelectedBank = getActivity().getIntent().getParcelableExtra(Constants.SELECTED_BANK_ACCOUNT);

        mProgressDialog = new ProgressDialog(getActivity());
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_withdraw_money_review));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_withdraw_money_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ImageView bankIconImageView = findViewById(R.id.bank_icon_image_view);
        final TextView bankNameTextView = findViewById(R.id.bank_name_text_view);
        final TextView bankAccountNumberTextView = findViewById(R.id.bank_account_number_text_view);
        final TextView amountTextView = findViewById(R.id.amount_text_view);
        mServiceChargeTextView = findViewById(R.id.service_charge_text_view);
        mNetAmountTextView = findViewById(R.id.net_amount_text_view);
        final LinearLayout descriptionViewHolder = findViewById(R.id.description_view_holder);
        final TextView descriptionTextView = findViewById(R.id.description_text_view);
        final Button withdrawMoneyButton = findViewById(R.id.withdraw_money_button);

        bankIconImageView.setImageResource(mSelectedBank.getBankIcon(getContext()));
        bankNameTextView.setText(mSelectedBank.getBankName());
        bankAccountNumberTextView.setText(mSelectedBank.getAccountNumber());

        amountTextView.setText(Utilities.formatTaka(getAmount()));
        mServiceChargeTextView.setText(Utilities.formatTaka(new BigDecimal(0.0)));
        mNetAmountTextView.setText(Utilities.formatTaka(getAmount().add(new BigDecimal(0.0))));

        if (mDescription == null || mDescription.isEmpty()) {
            descriptionViewHolder.setVisibility(View.GONE);
        } else {
            descriptionViewHolder.setVisibility(View.VISIBLE);
            descriptionTextView.setText(mDescription);
        }

        withdrawMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    String errorMessage = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmount),
                            WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (errorMessage == null) {
                        attemptWithdrawMoneyWithPinCheck();

                    } else {
                        showErrorDialog(errorMessage);
                    }
                } else
                    attemptWithdrawMoneyWithPinCheck();
                ;

            }
        });

        attemptGetServiceCharge();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
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
        mWithdrawMoneyRequest = new WithdrawMoneyRequest(mSelectedBank.getBankAccountId(), mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mWithdrawMoneyRequest);
        mWithdrawMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_WITHDRAW_MONEY,
                Constants.BASE_URL_SM + Constants.URL_WITHDRAW_MONEY, json, getActivity());
        mWithdrawMoneyTask.mHttpResponseListener = this;

        mWithdrawMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog(final String errorMessage) {
        new AlertDialog.Builder(getContext())
                .setMessage(errorMessage)
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
        mServiceChargeTextView.setText(Utilities.formatTaka(serviceCharge));
        mNetAmountTextView.setText(Utilities.formatTaka(getAmount().add(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mWithdrawMoneyRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_WITHDRAW_MONEY,
                Constants.BASE_URL_SM + Constants.URL_WITHDRAW_MONEY, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
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
                WithdrawMoneyResponse mWithdrawMoneyResponse = gson.fromJson(result.getJsonString(), WithdrawMoneyResponse.class);

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