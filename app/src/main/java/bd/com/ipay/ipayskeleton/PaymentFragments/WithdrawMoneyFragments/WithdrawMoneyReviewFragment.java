package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.WithdrawMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.WithdrawMoneyResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class WithdrawMoneyReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mWithdrawMoneyTask = null;
    private WithdrawMoneyResponse mWithdrawMoneyResponse;

    private ProgressDialog mProgressDialog;

    private double mAmount;
    private String mDescription;
    private long mBankAccountId;
    private String mBankName;
    private String mBankAccountNumber;
    private String mError_message;
    private int mBankCode;

    private LinearLayout mLinearLayoutDescriptionHolder;
    private TextView mBankNameView;
    private TextView mBankAccountNumberView;
    private TextView mDescriptionView;
    private View mDescriptionHolder;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private Button mWithdrawMoneyButton;
    private RoundedImageView mBankIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_withdraw_money_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);
        mBankAccountId = getActivity().getIntent().getLongExtra(Constants.BANK_ACCOUNT_ID, -1);
        mBankName = getActivity().getIntent().getStringExtra(Constants.BANK_NAME);
        mBankAccountNumber = getActivity().getIntent().getStringExtra(Constants.BANK_ACCOUNT_NUMBER);
        mBankCode = getActivity().getIntent().getIntExtra(Constants.BANK_CODE,0);
        Drawable bankIcon =  getResources().getDrawable(mBankCode);

        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mLinearLayoutDescriptionHolder = (LinearLayout) v.findViewById(R.id.layout_description_holder);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mDescriptionHolder = v.findViewById(R.id.description_holder);
        mBankNameView = (TextView) v.findViewById(R.id.textview_bank_name);
        mBankAccountNumberView = (TextView) v.findViewById(R.id.textview_account_number);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mWithdrawMoneyButton = (Button) v.findViewById(R.id.button_withdraw_money);
        mBankIcon = (RoundedImageView) v.findViewById(R.id.portrait);

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
                        attemptAddMoneyWithPinCheck();

                    } else {
                        showErrorDialog();
                    }
                } else
                    attemptAddMoneyWithPinCheck();

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

    private void attemptAddMoneyWithPinCheck() {
        if (WithdrawMoneyActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

            pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    attemptWithdrawMoney(pinInputDialogBuilder.getPin());
                }
            });
            pinInputDialogBuilder.build().show();
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

        WithdrawMoneyRequest mAddMoneyRequest = new WithdrawMoneyRequest(mBankAccountId, mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mAddMoneyRequest);
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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mWithdrawMoneyTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
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
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mWithdrawMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
            mWithdrawMoneyTask = null;

        }
    }
}