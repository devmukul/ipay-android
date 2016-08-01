package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.ProgressDialog;
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

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReceivedRequestReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAcceptRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private ProgressDialog mProgressDialog;

    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    private String mTitle;
    private long mRequestID;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDescriptionView;
    private TextView mTitleView;
    private View mDescriptionHolder;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mNetReceivedView;
    private Button mReceiveRequestMoneyButton;
    private boolean isPinRequired = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_received_request_review, container, false);
        getActivity().setTitle(R.string.send_money);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.INVOICE_RECEIVER_TAG);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);
        mTitle = getActivity().getIntent().getStringExtra(Constants.INVOICE_TITLE_TAG);
        mRequestID = (long) getActivity().getIntent().getSerializableExtra(Constants.MONEY_REQUEST_ID);

        mReceiverName = getActivity().getIntent().getStringExtra(Constants.NAME);
        mPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mTitleView = (TextView) v.findViewById(R.id.textview_title);
        mDescriptionHolder = v.findViewById(R.id.layout_description_holder);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetReceivedView = (TextView) v.findViewById(R.id.textview_net_received);
        mReceiveRequestMoneyButton = (Button) v.findViewById(R.id.button_send_money);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfileImageView.setProfilePicture(mPhotoUri, false);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

        if ((mDescription == null || mDescription.isEmpty()) && (mTitle == null || mTitle.isEmpty())) {
            mDescriptionHolder.setVisibility(View.GONE);
        } else {
            if (mDescription == null || mDescription.isEmpty()) {
                mDescriptionView.setVisibility(View.GONE);
            } else {
                mDescriptionView.setText(mDescription);
            }

            if (mTitle == null || mTitle.isEmpty()) {
                mTitleView.setVisibility(View.GONE);
            } else {
                mTitleView.setText(mTitle);
            }
        }

        mAmountView.setText(Utilities.formatTaka(mAmount));

        mReceiveRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempAcceptRequestWithPinCheck();
            }
        });

        attemptGetServiceCharge();

        return v;
    }

    private void attempAcceptRequestWithPinCheck() {
        if (this.isPinRequired) {
            final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

            pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    acceptRequestMoney(mRequestID, pinInputDialogBuilder.getPin());
                }
            });

            pinInputDialogBuilder.build().show();
        } else {
            acceptRequestMoney(mRequestID, null);
        }

    }

    private void acceptRequestMoney(long id, String pin) {
        if (mAcceptRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mAcceptRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, getActivity());
        mAcceptRequestTask.mHttpResponseListener = this;
        mAcceptRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mAcceptRequestTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();


        if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {
            try {
                mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mRequestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mAcceptRequestTask = null;

        }
    }

    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_SEND_MONEY;
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

        this.isPinRequired = isPinRequired;
    }
}
