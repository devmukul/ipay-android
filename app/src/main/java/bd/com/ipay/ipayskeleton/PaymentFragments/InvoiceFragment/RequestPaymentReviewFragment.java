package bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.SendInvoiceRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.SendInvoiceResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestPaymentReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSendInvoiceTask = null;
    private SendInvoiceResponse mSendInvoiceResponse;

    private ProgressDialog mProgressDialog;

    private String mReceiverMobileNumber;
    private String mDescription;
    private BigDecimal mVat;
    private BigDecimal mTotal;
    private BigDecimal mAmount;

    private String mReceiverName;
    private String mPhotoUri;
    private String mError_message;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mAmountView;
    private TextView mVatView;
    private TextView mTotalView;
    private TextView mServiceChargeView;
    private TextView mNetAmountView;

    private TextView mDescriptionView;
    private Button mCreateInvoiceButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_request_payment_review, container, false);

        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.INVOICE_RECEIVER_TAG);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);
        mAmount = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.AMOUNT));
        mTotal = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.TOTAL));
        if (getActivity().getIntent().getStringExtra(Constants.VAT).equals(""))
            mVat = new BigDecimal(0);
        else mVat = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.VAT));

        mReceiverName = getArguments().getString(Constants.NAME);
        mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mVatView = (TextView) v.findViewById(R.id.textview_vat);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetAmountView = (TextView) v.findViewById(R.id.textview_net_amount);

        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mCreateInvoiceButton = (Button) v.findViewById(R.id.button_create_invoice);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfileImageView.setProfilePicture(mPhotoUri, false);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        BigDecimal Vat = mAmount.multiply(mVat.divide(new BigDecimal(100)));
        mNameView.setText(mReceiverName);
        mMobileNumberView.setText(mReceiverMobileNumber);
        mAmountView.setText(Utilities.formatTaka(mAmount));
        mVatView.setText(Utilities.formatTaka(Vat));

        mTotalView.setText(Utilities.formatTaka(mTotal));
        mDescriptionView.setText(mDescription);

        mCreateInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isValueAvailable(RequestPaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(RequestPaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    mError_message = InputValidator.isValidAmount(getActivity(), mTotal,
                            RequestPaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            RequestPaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (mError_message == null) {
                        attemptSendInvoice();

                    } else {
                        showErrorDialog();
                    }
                }
            }
        });

        // Check if min or max amount is available
        if (!Utilities.isValueAvailable(RequestPaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(RequestPaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRuleWithServiceCharge(Constants.SERVICE_ID_REQUEST_PAYMENT);
        else
            attemptGetServiceCharge();
        return v;
    }

    private void showErrorDialog() {
        mError_message = mError_message.replace(getString(R.string.payment_amount), getString(R.string.payment_total_amount));

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

    private void attemptSendInvoice() {
        if (mSendInvoiceTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_sending_invoice));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        SendInvoiceRequest mCreateInvoiceRequest = new SendInvoiceRequest(mAmount, mReceiverMobileNumber, mDescription, null, mVat);
        Gson gson = new Gson();
        String json = gson.toJson(mCreateInvoiceRequest);
        mSendInvoiceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVOICE,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_SEND_INVOICE, json, getActivity());
        mSendInvoiceTask.mHttpResponseListener = this;
        mSendInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_REQUEST_PAYMENT;
    }

    @Override
    public BigDecimal getAmount() {
        return mTotal;
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mNetAmountView.setText(Utilities.formatTaka(mTotal.subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        RequestPaymentActivity.mMandatoryBusinessRules.setIS_PIN_REQUIRED(isPinRequired);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mProgressDialog.dismiss();
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVOICE)) {

            try {
                mSendInvoiceResponse = gson.fromJson(result.getJsonString(), SendInvoiceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getActivity().setResult(Activity.RESULT_OK);
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSendInvoiceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSendInvoiceResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();
            }
            mSendInvoiceTask = null;
        }

        mProgressDialog.dismiss();
    }

}
