package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private TopupResponse mTopupResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private double mAmount;
    private String mMobileNumber;
    private int mAccountType;
    private int mMobileNumberType;
    private String mCountryCode;
    private int mOperatorCode;
    String mError_message;

    private TextView mMobileNumberView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private Button mTopupButton;

    private View mServiceChargeHolder;
    private View mTopUpHolder;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mobile_topup_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mMobileNumberType = getActivity().getIntent().getIntExtra(Constants.MOBILE_NUMBER_TYPE, 1);
        mOperatorCode = getActivity().getIntent().getIntExtra(Constants.OPERATOR_CODE, 0);
        mCountryCode = getActivity().getIntent().getStringExtra(Constants.COUNTRY_CODE);

        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mTopupButton = (Button) v.findViewById(R.id.button_topup);

        mServiceChargeHolder = v.findViewById(R.id.service_charge_holder);
        mTopUpHolder = v.findViewById(R.id.topup_holder);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mMobileNumber = pref.getString(Constants.USERID, "");
        mAccountType = pref.getInt(Constants.ACCOUNT_TYPE, 1);

        mMobileNumberView.setText(mMobileNumber);

        mAmountView.setText(Utilities.formatTaka(mAmount));

        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mError_message = Utilities.isValidAmount(getActivity(), new BigDecimal(mAmount), TopUpActivity.MIN_AMOUNT_PER_PAYMENT, TopUpActivity.MAX_AMOUNT_PER_PAYMENT);

                if (mError_message == null) {
                    final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                    pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            attemptTopUp(pinInputDialogBuilder.getPin());
                        }
                    });

                    pinInputDialogBuilder.build().show();

                } else {
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
            }
        });

        // Check if Min or max amount is available
        if (!Utilities.isValueAvailable(TopUpActivity.MAX_AMOUNT_PER_PAYMENT)
                && !Utilities.isValueAvailable(TopUpActivity.MIN_AMOUNT_PER_PAYMENT))
            attemptGetBusinessRulewithServiceCharge(Constants.SERVICE_ID_SEND_MONEY);
        else
            attemptGetServiceCharge();

        return v;
    }

    private void attemptTopUp(String pin) {
        TopupRequest mTopupRequestModel = new TopupRequest(Long.parseLong(mMobileNumber.replaceAll("[^0-9]", "")),
                mMobileNumber, mMobileNumberType, mOperatorCode, mAmount,
                mCountryCode, mAccountType, Constants.DEFAULT_USER_CLASS, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mTopupRequestModel);
        mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
        mTopupTask.mHttpResponseListener = this;
        mTopupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_TOP_UP;
    }

    @Override
    public BigDecimal getAmount() {
        return new BigDecimal(mAmount);
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        if (serviceCharge == null || serviceCharge.equals(BigDecimal.ZERO)) {
            mServiceChargeHolder.setVisibility(View.GONE);
            mTopUpHolder.setVisibility(View.GONE);
        } else {
            mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
            mTotalView.setText(Utilities.formatTaka(getAmount().subtract(serviceCharge)));
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mTopupTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_TOPUP_REQUEST)) {

            try {
                mTopupResponse = gson.fromJson(result.getJsonString(), TopupResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                    // TODO: Save transaction in database
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // TODO: Save transaction in database
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mTopupTask = null;

        }
    }
}
