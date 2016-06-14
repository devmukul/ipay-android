package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

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
import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Customview.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.InvoiceItemList;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.SaveInvoiceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.SaveInvoiceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.SendInvoiceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.SendInvoiceResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceReviewFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSaveInvoiceTask = null;
    private SaveInvoiceResponse mSaveInvoiceResponse;


    private HttpRequestPostAsyncTask mSendInvoiceTask = null;
    private SendInvoiceResponse mSendInvoiceResponse;

    private ProgressDialog mProgressDialog;

    private String mReceiverMobileNumber;
    private String mItemName;
    private String mDescription;
    private BigDecimal mQuantity;
    private BigDecimal mRate;
    private BigDecimal mVat;
    private BigDecimal mTotal;
    private BigDecimal mAmount;

    private String mReceiverName;
    private String mPhotoUri;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mQuantityView;
    private TextView mRateView;
    private TextView mAmountView;
    private TextView mVatView;
    private TextView mTotalView;

    private TextView mTitleView;
    private TextView mDescriptionView;
    private Button mCreateInvoiceButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_invoice_review, container, false);

        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.INVOICE_RECEIVER_TAG);
        mItemName = getActivity().getIntent().getStringExtra(Constants.INVOICE_ITEM_NAME_TAG);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);
        mQuantity = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.INVOICE_QUANTITY_TAG));
        mRate = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.INVOICE_RATE_TAG));
        if (getActivity().getIntent().getStringExtra(Constants.VAT).equals(""))
            mVat = new BigDecimal(0);
        else mVat = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.VAT));
        mTotal = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.TOTAL));

        mReceiverName = getArguments().getString(Constants.NAME);
        mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mQuantityView = (TextView) v.findViewById(R.id.textview_quantity);
        mRateView = (TextView) v.findViewById(R.id.textview_rate);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mVatView = (TextView) v.findViewById(R.id.textview_vat);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);

        mTitleView = (TextView) v.findViewById(R.id.textview_title);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mCreateInvoiceButton = (Button) v.findViewById(R.id.button_create_invoice);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfileImageView.setInformation(mPhotoUri, mReceiverName);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mAmount = mQuantity.multiply(mRate);
        mNameView.setText(mReceiverName);
        mMobileNumberView.setText(mReceiverMobileNumber);
        mQuantityView.setText(Utilities.formatTaka(mQuantity));
        mRateView.setText(Utilities.formatTaka(mRate));
        mAmountView.setText(Utilities.formatTaka(mAmount));
        mVatView.setText(Utilities.formatTaka(mVat));

        mTotalView.setText(Utilities.formatTaka(mTotal));
        mTitleView.setText(mItemName);
        mDescriptionView.setText(mDescription);

        mCreateInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSaveInvoice();

            }
        });

        return v;
    }

    private void attemptSendInvoice(int requestId) {
        if (mSendInvoiceTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_sending_invoice));
        mProgressDialog.show();

        SendInvoiceRequest mCreateInvoiceRequest = new SendInvoiceRequest(mTotal, mReceiverMobileNumber, mDescription, requestId, mVat);
        Gson gson = new Gson();
        String json = gson.toJson(mCreateInvoiceRequest);
        mSendInvoiceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVOICE,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_SEND_INVOICE, json, getActivity());
        mSendInvoiceTask.mHttpResponseListener = this;
        mSendInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptSaveInvoice() {
        if (mSaveInvoiceTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_sending_invoice));
        mProgressDialog.show();

        ArrayList<InvoiceItemList> invoiceItemList = new ArrayList<InvoiceItemList>();
        InvoiceItemList tempInvoiceItemList = new InvoiceItemList(mDescription, mItemName, Integer.valueOf(mQuantity.intValue()),
                Integer.valueOf(mRate.intValue()), Integer.valueOf(mTotal.intValue()));
        invoiceItemList.add(tempInvoiceItemList);

        SaveInvoiceRequest mSaveInvoiceRequest = new SaveInvoiceRequest("", mReceiverMobileNumber, "", Integer.valueOf(mVat.intValue()), invoiceItemList);
        Gson gson = new Gson();
        String json = gson.toJson(mSaveInvoiceRequest);
        mSaveInvoiceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SAVE_INVOICE,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_SAVE_INVOICE, json, getActivity());
        mSaveInvoiceTask.mHttpResponseListener = this;
        mSaveInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSaveInvoiceTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SAVE_INVOICE)) {

            try {
                mSaveInvoiceResponse = gson.fromJson(result.getJsonString(), SaveInvoiceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getActivity().setResult(Activity.RESULT_OK);
                    int requestId = mSaveInvoiceResponse.getInvoiceId();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSaveInvoiceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    attemptSendInvoice(requestId);
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSaveInvoiceResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();
            }

            mSaveInvoiceTask = null;
        }

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
