package bd.com.ipay.ipayskeleton.Customview.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Customview.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyRequestReviewDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRejectRequestTask = null;
    private HttpRequestPostAsyncTask mAcceptRequestTask = null;
    private RequestMoneyAcceptRejectOrCancelResponse mRequestMoneyAcceptRejectOrCancelResponse;

    private SharedPreferences pref;

    private BigDecimal mAmount;
    private BigDecimal mServiceCharge;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mSenderMobileNumber;
    private String mPhotoUri;
    private long mMoneyRequestId;
    private String mTitle;
    private String mDescription;

    private ProgressDialog mProgressDialog;
    private ReviewDialogFinishListener mReviewFinishListener;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
//    private TextView mDescriptionView;
    private TextView mTitleView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mNetReceivedView;
    private EditText mPinField;

    public RequestMoneyRequestReviewDialog(Context context, long moneyRequestId, String receiverMobileNumber,
                           String receiverName, String photoUri, BigDecimal amount, BigDecimal serviceCharge,
                           String title, String description, ReviewDialogFinishListener reviewFinishListener) {
        super(context);

        this.mMoneyRequestId = moneyRequestId;
        this.mReceiverMobileNumber = receiverMobileNumber;
        this.mReceiverName = receiverName;
        this.mPhotoUri = photoUri;
        this.mAmount = amount;
        this.mServiceCharge = serviceCharge;
        this.mTitle = title;
        this.mDescription = description;
        this.mReviewFinishListener = reviewFinishListener;

        initializeView();
    }

    public void initializeView() {
        customView(R.layout.dialog_request_money_request_review, true);

        View v = this.build().getCustomView();

        mProgressDialog = new ProgressDialog(context);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
//        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mTitleView = (TextView) v.findViewById(R.id.textview_title);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetReceivedView = (TextView) v.findViewById(R.id.textview_net_received);
        mPinField = (EditText) v.findViewById(R.id.pin);

        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mSenderMobileNumber = pref.getString(Constants.USERID, "");

        mProfileImageView.setInformation(mPhotoUri, mReceiverName);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

//        if (mDescription == null || mDescription.isEmpty()) {
//            mDescriptionView.setVisibility(View.GONE);
//        } else {
//            mDescriptionView.setText(mDescription);
//        }

        if (mTitle == null || mTitle.isEmpty()) {
            mTitleView.setVisibility(View.GONE);
        } else {
            mTitleView.setText(mTitle);
        }

        mAmountView.setText(Utilities.formatTaka(mAmount));
        mServiceChargeView.setText(Utilities.formatTaka(mServiceCharge));
        mNetReceivedView.setText(Utilities.formatTaka(mAmount.subtract(mServiceCharge)));

        positiveText(R.string.send_money);
        negativeText(R.string.reject);

        onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String pin = mPinField.getText().toString();
                if (pin.isEmpty())
                    Toast.makeText(context, R.string.failed_empty_pin, Toast.LENGTH_LONG).show();
                else {
                    acceptRequestMoney(mMoneyRequestId, pin);
                }
            }
        });

        onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                String pin = mPinField.getText().toString();
                if (pin.isEmpty())
                    Toast.makeText(context, R.string.failed_empty_pin, Toast.LENGTH_LONG).show();
                else {
                    rejectRequestMoney(mMoneyRequestId, pin);
                }
            }
        });
    }

    private void acceptRequestMoney(long id, String pin) {
        if (mAcceptRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(context.getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mAcceptRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, context);
        mAcceptRequestTask.mHttpResponseListener = this;
        mAcceptRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void rejectRequestMoney(long id, String pin) {
        if (mRejectRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(context.getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest =
                new RequestMoneyAcceptRejectOrCancelRequest(id, pin);
        Gson gson = new Gson();
        String json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
        mRejectRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_REQUESTS_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REJECT_NOTIFICATION_REQUEST, json, context);
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.show();
            mAcceptRequestTask = null;
            mRejectRequestTask = null;
            if (context != null)
                Toast.makeText(context, R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_REJECT_REQUESTS_MONEY)) {

            try {
                mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (context != null)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if (mReviewFinishListener != null)
                        mReviewFinishListener.onReviewFinish();

                } else {
                    if (context != null)
                        Toast.makeText(context, mRequestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (context != null)
                    Toast.makeText(context, R.string.could_not_reject_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mRejectRequestTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {
            try {
                mRequestMoneyAcceptRejectOrCancelResponse = gson.fromJson(resultList.get(2),
                        RequestMoneyAcceptRejectOrCancelResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    String message = mRequestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (context != null)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                    if (mReviewFinishListener != null)
                        mReviewFinishListener.onReviewFinish();

                } else {
                    if (context != null)
                        Toast.makeText(context, mRequestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (context != null)
                    Toast.makeText(context, R.string.could_not_accept_money_request, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mAcceptRequestTask = null;

        }
    }
}
