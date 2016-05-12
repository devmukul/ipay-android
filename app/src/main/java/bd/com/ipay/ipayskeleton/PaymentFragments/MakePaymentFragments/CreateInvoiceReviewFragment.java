package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Customview.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.CreateInvoiceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.CreateInvoiceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceReviewFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateInvoiceTask = null;
    private CreateInvoiceResponse mCreateInvoiceResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private BigDecimal mAmount;
    private BigDecimal mVat;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private String mDescription;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDescriptionView;
    private View mDescriptionHolder;
    private TextView mAmountView;
    private TextView mVatView;
    private TextView mTotalView;
    private Button mCreateInvoiceButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_invoice_review, container, false);

        mAmount = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.AMOUNT));
        mVat = new BigDecimal(getActivity().getIntent().getStringExtra(Constants.VAT));
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION);

        mReceiverName = getArguments().getString(Constants.NAME);
        mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mDescriptionHolder = v.findViewById(R.id.description_holder);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mVatView = (TextView) v.findViewById(R.id.textview_vat);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mCreateInvoiceButton = (Button) v.findViewById(R.id.button_create_invoice);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mProfileImageView.setInformation(mPhotoUri, mReceiverName);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

        if (mDescription == null || mDescription.isEmpty()) {
            mDescriptionHolder.setVisibility(View.GONE);
        } else {
            mDescriptionView.setText(mDescription);
        }

        mAmountView.setText(Utilities.formatTaka(mAmount));
        mVatView.setText(Utilities.formatTaka(mVat));
        mTotalView.setText(Utilities.formatTaka(mAmount.add(mVat)));

        mCreateInvoiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        attemptSendInvoice(pinInputDialogBuilder.getPin());
                    }
                });

                pinInputDialogBuilder.build().show();
            }
        });

        return v;
    }

    private void attemptSendInvoice(String pin) {
        if (mCreateInvoiceTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_sending_invoice));
        mProgressDialog.show();

        CreateInvoiceRequest mCreateInvoiceRequest = new CreateInvoiceRequest(mDescription,
                mAmount, mVat, mReceiverMobileNumber, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mCreateInvoiceRequest);
        mCreateInvoiceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CREATE_INVOICE,
                Constants.BASE_URL_SM + Constants.URL_PAYMENT_CREATE_INVOICE, json, getActivity());
        mCreateInvoiceTask.mHttpResponseListener = this;
        mCreateInvoiceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mCreateInvoiceTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_CREATE_INVOICE)) {

            if (resultList.size() > 2) {
                try {
                    mCreateInvoiceResponse = gson.fromJson(resultList.get(2), CreateInvoiceResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        getActivity().setResult(Activity.RESULT_OK);
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mCreateInvoiceResponse.getMessage(), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mCreateInvoiceResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_invoice_creation, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mCreateInvoiceTask = null;
        }
    }

}
