package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

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

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentMakingActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.PaymentResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class PaymentReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mPaymentTask = null;
    private PaymentResponse mPaymentResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    String mError_message;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDescriptionView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mNetReceivedView;
    private Button mPaymentButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_payment_review, container, false);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.INVOICE_RECEIVER_TAG);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);

        mReceiverName = getArguments().getString(Constants.NAME);
        mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetReceivedView = (TextView) v.findViewById(R.id.textview_net_received);
        mPaymentButton = (Button) v.findViewById(R.id.button_payment);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mProfileImageView.setInformation(mReceiverMobileNumber, mPhotoUri, mReceiverName, false);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

        if (mDescription == null || mDescription.isEmpty()) {
            mDescriptionView.setVisibility(View.GONE);
        } else {
            mDescriptionView.setText(mDescription);
        }

        mAmountView.setText(Utilities.formatTaka(mAmount));

        mPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isValueAvailable(PaymentMakingActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(PaymentMakingActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    mError_message = InputValidator.isValidAmount(getActivity(), mAmount,
                            PaymentMakingActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            PaymentMakingActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (mError_message == null) {
                        if (PaymentMakingActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
                            final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                            pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    attemptPayment(pinInputDialogBuilder.getPin());
                                }
                            });

                            pinInputDialogBuilder.build().show();
                        }

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
                } else {
                    if (PaymentMakingActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
                        final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                        pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                attemptPayment(pinInputDialogBuilder.getPin());
                            }
                        });

                        pinInputDialogBuilder.build().show();
                    }
                }
            }
        });

        // Check if Min or max amount is available
        if (!Utilities.isValueAvailable(PaymentMakingActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(PaymentMakingActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRulewithServiceCharge(Constants.SERVICE_ID_MAKE_PAYMENT);
        else
            attemptGetServiceCharge();
        return v;
    }

    private void attemptPayment(String pin) {
        if (mPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_payment));
        mProgressDialog.show();
        PaymentRequest mPaymentRequest = new PaymentRequest(
                ContactEngine.formatMobileNumberBD(mReceiverMobileNumber),
                mAmount.toString(), mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mPaymentRequest);
        mPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT, json, getActivity());
        mPaymentTask.mHttpResponseListener = this;
        mPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_MAKE_PAYMENT;
    }

    @Override
    public BigDecimal getAmount() {
        return mAmount;
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mNetReceivedView.setText(Utilities.formatTaka(mAmount.subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        PaymentMakingActivity.mMandatoryBusinessRules.setIS_PIN_REQUIRED(isPinRequired);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mPaymentTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.payment_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_PAYMENT)) {

            try {
                mPaymentResponse = gson.fromJson(result.getJsonString(), PaymentResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mPaymentResponse.getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mPaymentResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
            mPaymentTask = null;

        }
    }
}
