package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyByBankResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyFromBankReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAddMoneyTask = null;

    private ProgressDialog mProgressDialog;

    private double mAmount;
    private String mDescription;
    private UserBankClass mSelectedBank;

    private TextView mServiceChargeTextView;
    private TextView mNetAmountTextView;

    private Tracker mTracker;

    private AddMoneyRequest mAddMoneyRequest;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_money_by_bank_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final ImageView bankIconImageView = findViewById(R.id.bank_icon_image_view);
        final TextView bankNameTextView = findViewById(R.id.bank_name_text_view);
        final TextView bankAccountNumberTextView = findViewById(R.id.bank_account_number_text_view);
        final TextView amountTextView = findViewById(R.id.amount_text_view);
        final LinearLayout descriptionViewHolder = findViewById(R.id.description_view_holder);
        final TextView descriptionTextView = findViewById(R.id.description_text_view);
        final Button addMoneyButton = findViewById(R.id.add_money_button);

        bankIconImageView.setImageResource(mSelectedBank.getBankIcon(getContext()));
        bankNameTextView.setText(mSelectedBank.getBankName());
        bankAccountNumberTextView.setText(mSelectedBank.getAccountNumber());
        mServiceChargeTextView = findViewById(R.id.service_charge_text_view);
        mNetAmountTextView = findViewById(R.id.net_amount_text_view);
        amountTextView.setText(Utilities.formatTaka(getAmount()));
        mServiceChargeTextView.setText(Utilities.formatTaka(new BigDecimal(0.0)));
        mNetAmountTextView.setText(Utilities.formatTaka(getAmount().subtract(new BigDecimal(0.0))));
        if (TextUtils.isEmpty(mDescription)) {
            descriptionViewHolder.setVisibility(View.GONE);
        } else {
            descriptionViewHolder.setVisibility(View.VISIBLE);
            descriptionTextView.setText(mDescription);
        }

        addMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isValueAvailable(AddMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(AddMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    final String errorMessage = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmount),
                            AddMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            AddMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (errorMessage == null) {
                        attemptAddMoneyWithPinCheck();

                    } else {
                        showErrorDialog(errorMessage);
                    }
                } else
                    attemptAddMoneyWithPinCheck();
            }
        });

        // Check if Min or max amount is available
        if (!Utilities.isValueAvailable(AddMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(AddMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRuleWithServiceCharge(Constants.SERVICE_ID_ADD_MONEY_BY_BANK);
        else
            attemptGetServiceCharge();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_add_money_review));
    }

    private void attemptAddMoneyWithPinCheck() {
        if (AddMoneyActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptAddMoney(pin);
                }
            });

        } else {
            attemptAddMoney(null);
        }
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mAddMoneyRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_ADD_MONEY,
                Constants.BASE_URL_SM + Constants.URL_ADD_MONEY, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
    }

    private void attemptAddMoney(String pin) {
        if (mAddMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mAddMoneyRequest = new AddMoneyRequest(mSelectedBank.getBankAccountId(), mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mAddMoneyRequest);
        mAddMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_MONEY,
                Constants.BASE_URL_SM + Constants.URL_ADD_MONEY, json, getActivity());
        mAddMoneyTask.mHttpResponseListener = this;

        mAddMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        return Constants.SERVICE_ID_ADD_MONEY_BY_BANK;
    }

    @Override
    public BigDecimal getAmount() {
        return new BigDecimal(mAmount);
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeTextView.setText(Utilities.formatTaka(serviceCharge));
        mNetAmountTextView.setText(Utilities.formatTaka(getAmount().subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        AddMoneyActivity.mMandatoryBusinessRules.setIS_PIN_REQUIRED(isPinRequired);

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mAddMoneyTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_MONEY)) {

            try {
                final AddMoneyByBankResponse mAddMoneyByBankResponse = gson.fromJson(result.getJsonString(), AddMoneyByBankResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mAddMoneyByBankResponse.getMessage(), Toast.LENGTH_LONG);
                    getActivity().setResult(Activity.RESULT_OK);
                    // Exit the Add money activity and return to HomeActivity
                    getActivity().finish();

                    //Google Analytic event
                    Utilities.sendSuccessEventTracker(mTracker, "Add Money By Bank", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                    if (getActivity() != null)
                        ((MyApplication) getActivity().getApplication()).launchLoginPage(mAddMoneyByBankResponse.getMessage());
                    Utilities.sendBlockedEventTracker(mTracker, "Add Money By Bank", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                    Toast.makeText(getActivity(), mAddMoneyByBankResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    SecuritySettingsActivity.otpDuration = mAddMoneyByBankResponse.getOtpValidFor();
                    launchOTPVerification();
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mAddMoneyByBankResponse.getMessage(), Toast.LENGTH_LONG);

                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Add Money By Bank", ProfileInfoCacheManager.getAccountId(), mAddMoneyByBankResponse.getMessage(), Double.valueOf(mAmount).longValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.add_money_failed, Toast.LENGTH_LONG);
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }

            mProgressDialog.dismiss();
            mAddMoneyTask = null;

        }
    }
}