package bd.com.ipay.ipayskeleton.PaymentFragments.RequestPaymentFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SentReceivedRequestPaymentReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAcceptRequestTask = null;

    private HttpRequestPostAsyncTask mCancelRequestTask = null;

    private HttpRequestPostAsyncTask mRejectRequestTask = null;

    private PaymentAcceptRejectOrCancelRequest mRequestPaymentAcceptRejectOrCancelRequest;
    private PaymentAcceptRejectOrCancelResponse mRequestPaymentAcceptRejectOrCancelResponse;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private ProgressDialog mProgressDialog;

    private int mRequestType;
    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    private long mRequestID;
    private String mTransactionID;
    private int mStatus;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDescriptionTagView;
    private TextView mDescriptionView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mNetAmountView;
    private Button mRejectButton;
    private Button mAcceptButton;
    private Button mCancelButton;

    private boolean isPinRequired = true;
    private boolean switchedFromTransactionHistory = false;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_request_money_sent_review));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sent_received_request_review, container, false);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER_MOBILE_NUMBER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);
        mReceiverName = getActivity().getIntent().getStringExtra(Constants.NAME);
        mStatus = getActivity().getIntent().getIntExtra(Constants.STATUS, Constants.HTTP_RESPONSE_STATUS_PROCESSING);
        mPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);
        mRequestType = getActivity().getIntent()
                .getIntExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);

        switchedFromTransactionHistory = getActivity().getIntent()
                .getBooleanExtra(Constants.SWITCHED_FROM_TRANSACTION_HISTORY, false);

        if (switchedFromTransactionHistory)
            mTransactionID = getActivity().getIntent().getStringExtra(Constants.TRANSACTION_ID);
        else
            mRequestID = (long) getActivity().getIntent().getSerializableExtra(Constants.MONEY_REQUEST_ID);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDescriptionTagView = (TextView) v.findViewById(R.id.description);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetAmountView = (TextView) v.findViewById(R.id.textview_net_amount);

        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);
        mCancelButton = (Button) v.findViewById(R.id.button_cancel);

        mProgressDialog = new ProgressDialog(getActivity());

        getActivity().setTitle(R.string.request_payment);

        mProfileImageView.setProfilePicture(mPhotoUri, false);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

        if (mDescription == null || mDescription.isEmpty()) {
            mDescriptionTagView.setVisibility(View.GONE);
            mDescriptionView.setVisibility(View.GONE);
        } else
            mDescriptionView.setText(mDescription);

        if (mStatus == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            if (mRequestType == Constants.REQUEST_TYPE_RECEIVED_REQUEST) {
                mAcceptButton.setVisibility(View.VISIBLE);
                mRejectButton.setVisibility(View.VISIBLE);
                mCancelButton.setVisibility(View.GONE);
            } else {
                mAcceptButton.setVisibility(View.GONE);
                mRejectButton.setVisibility(View.GONE);
                mCancelButton.setVisibility(View.VISIBLE);
            }
        } else {
            mAcceptButton.setVisibility(View.GONE);
            mRejectButton.setVisibility(View.GONE);
            mCancelButton.setVisibility(View.GONE);
        }

        mAmountView.setText(Utilities.formatTaka(mAmount));

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.ACCEPT_REQUEST)
            public void onClick(View v) {
                attempAcceptRequestWithPinCheck();
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.REJECT_REQUEST)
            public void onClick(View v) {
                MaterialDialog.Builder rejectDialog = new MaterialDialog.Builder(getActivity());
                rejectDialog.content(R.string.confirm_request_rejection);
                rejectDialog.positiveText(R.string.yes);
                rejectDialog.negativeText(R.string.no);
                rejectDialog.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        rejectRequestPayment();
                    }
                });
                rejectDialog.show();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.CANCEL_REQUEST)
            public void onClick(View v) {
                showAlertDialogue(getString(R.string.cancel_money_request_confirm), mRequestID);
            }
        });

        attemptGetServiceCharge();

        return v;
    }

    private void attempAcceptRequestWithPinCheck() {
        if (this.isPinRequired) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    acceptRequestPayment(pin);
                }
            });
        } else {
            acceptRequestPayment(null);
        }

    }

    private void showAlertDialogue(String msg, final long id) {
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setTitle(R.string.confirm_query);
        alertDialogue.setMessage(msg);

        alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                cancelRequestPayment();
            }
        });

        alertDialogue.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        alertDialogue.show();
    }

    private void cancelRequestPayment() {
        if (mCancelRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_cancelling));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        // No PIN needed for now to place a request from me
        if (!switchedFromTransactionHistory) {
            mRequestPaymentAcceptRejectOrCancelRequest =
                    new PaymentAcceptRejectOrCancelRequest(mRequestID, null);
        } else {
            mRequestPaymentAcceptRejectOrCancelRequest =
                    new PaymentAcceptRejectOrCancelRequest(mTransactionID, null);
        }

        Gson gson = new Gson();
        String json = gson.toJson(mRequestPaymentAcceptRejectOrCancelRequest);
        mCancelRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CANCEL_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_CANCEL_NOTIFICATION_REQUEST, json, getActivity());
        mCancelRequestTask.mHttpResponseListener = this;
        mCancelRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void rejectRequestPayment() {
        if (mRejectRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_rejecting));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        if (!switchedFromTransactionHistory) {
            mRequestPaymentAcceptRejectOrCancelRequest =
                    new PaymentAcceptRejectOrCancelRequest(mRequestID);
        } else mRequestPaymentAcceptRejectOrCancelRequest =
                new PaymentAcceptRejectOrCancelRequest(mTransactionID);

        Gson gson = new Gson();
        String json = gson.toJson(mRequestPaymentAcceptRejectOrCancelRequest);
        mRejectRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REJECT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_REJECT_NOTIFICATION_REQUEST, json, getActivity());
        mRejectRequestTask.mHttpResponseListener = this;
        mRejectRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void acceptRequestPayment(String pin) {
        if (mAcceptRequestTask != null) {
            return;
        }

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_accepted));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);

        if (!switchedFromTransactionHistory) {
            mRequestPaymentAcceptRejectOrCancelRequest =
                    new PaymentAcceptRejectOrCancelRequest(mRequestID, pin);
        } else {
            mRequestPaymentAcceptRejectOrCancelRequest =
                    new PaymentAcceptRejectOrCancelRequest(mTransactionID, pin);
        }

        Gson gson = new Gson();
        String json = gson.toJson(mRequestPaymentAcceptRejectOrCancelRequest);
        mAcceptRequestTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, json, getActivity());
        mAcceptRequestTask.mHttpResponseListener = this;
        mAcceptRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mRequestPaymentAcceptRejectOrCancelRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_ACCEPT_PAYMENT_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_ACCEPT_NOTIFICATION_REQUEST, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_CANCEL_PAYMENT_REQUEST)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    mRequestPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                            PaymentAcceptRejectOrCancelResponse.class);
                    String message = mRequestPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), message, Toast.LENGTH_LONG);

                    if (switchedFromTransactionHistory) {
                        Utilities.finishLauncherActivity(getActivity());
                    } else
                        getActivity().finish();
                    // ((RequestPaymentActivity) getActivity()).switchToSentPaymentRequestsFragment();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG);
                }

            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.could_not_cancel_money_request, Toast.LENGTH_LONG);
            }

            mProgressDialog.dismiss();
            mCancelRequestTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST)) {

            try {
                mRequestPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        PaymentAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRequestPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        if (switchedFromTransactionHistory)
                            Utilities.finishLauncherActivity(getActivity());
                        else
                            getActivity().onBackPressed();
                    }

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED ||
                        result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                    Toaster.makeText(getActivity(), mRequestPaymentAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG);
                    SecuritySettingsActivity.otpDuration = mRequestPaymentAcceptRejectOrCancelResponse.getOtpValidFor();
                    launchOTPVerification();
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRequestPaymentAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.could_not_accept_money_request, Toast.LENGTH_LONG);
            }
            mProgressDialog.dismiss();
            mAcceptRequestTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_REJECT_PAYMENT_REQUEST)) {

            try {
                mRequestPaymentAcceptRejectOrCancelResponse = gson.fromJson(result.getJsonString(),
                        PaymentAcceptRejectOrCancelResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRequestPaymentAcceptRejectOrCancelResponse.getMessage();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                        if (switchedFromTransactionHistory)
                            Utilities.finishLauncherActivity(getActivity());
                        else
                            getActivity().onBackPressed();
                    }

                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRequestPaymentAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.could_not_reject_money_request, Toast.LENGTH_LONG);
            }

            mProgressDialog.dismiss();
            mRejectRequestTask = null;

        }
    }

    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_REQUEST_PAYMENT;
    }

    @Override
    public BigDecimal getAmount() {
        return mAmount;
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mNetAmountView.setText(Utilities.formatTaka(mAmount.subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        this.isPinRequired = isPinRequired;
    }

}

