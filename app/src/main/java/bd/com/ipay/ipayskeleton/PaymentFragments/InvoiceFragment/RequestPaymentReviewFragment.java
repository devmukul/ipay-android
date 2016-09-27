package bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.SendInvoiceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.SendInvoiceResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestPaymentReviewFragment extends Fragment implements HttpResponseListener {

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

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mAmountView;
    private TextView mVatView;
    private TextView mTotalView;

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
                attemptSendInvoice();

            }
        });

        return v;
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
    public void httpResponseReceiver(HttpResponseObject result) {
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
