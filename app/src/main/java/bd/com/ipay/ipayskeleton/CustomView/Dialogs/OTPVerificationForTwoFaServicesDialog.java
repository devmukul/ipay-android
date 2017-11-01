package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.SendNewPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFaServiceAccomplishWithOTPResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFaServiceListWithOTPRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.CustomCountDownTimer;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationForTwoFaServicesDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private Activity context;

    private static String mDesiredRequest;

    private HttpRequestPostAsyncTask mHttpPostAsyncTask;

    private HttpRequestPutAsyncTask mHttpPutAsyncTask;

    private String json;
    private String mOTP;
    private String mUri;

    private EditText mOTPEditText;
    private Button mActivateButton;
    private Button mCancelButton;
    private Button mResendOTPButton;
    private View view;

    private MaterialDialog mOTPInputDialog;
    private ProgressDialog mProgressDialog;

    public dismissListener mDismissListener;

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    public OTPVerificationForTwoFaServicesDialog(@NonNull Activity context, String json, String desiredRequest, String mUri) {
        super(context);
        this.context = context;
        this.mDesiredRequest = desiredRequest;
        this.json = json;
        this.mUri = mUri;
        initializeView();
    }

    public OTPVerificationForTwoFaServicesDialog(Activity context) {
        super(context);

    }

    private void initializeView() {
        mOTPInputDialog = new MaterialDialog.Builder(this.getContext())
                .title(R.string.title_otp_verification_for_change_password)
                .customView(R.layout.dialog_otp_verification_change_password, true)
                .show();

        view = mOTPInputDialog.getCustomView();

        mOTPEditText = (EditText) view.findViewById(R.id.otp_edittext);
        mActivateButton = (Button) view.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) view.findViewById(R.id.buttonResend);
        mCancelButton = (Button) view.findViewById(R.id.buttonCancel);

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCancelable(false);

        setSMSBroadcastReceiver();
        setCountDownTimer();
        setButtonActions();

    }

    private void setButtonActions() {
        mActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiding the keyboard after verifying OTP
                Utilities.hideKeyboard(context, v);
                if (Utilities.isConnectionAvailable(context)) verifyInput();
                else if (context != null)
                    Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOTPInputDialog.dismiss();

            }
        });
        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setSMSBroadcastReceiver() {//enable broadcast receiver to get the text message to get the OTP
        mEnableDisableSMSBroadcastReceiver = new EnableDisableSMSBroadcastReceiver();

        mEnableDisableSMSBroadcastReceiver.enableBroadcastReceiver(getContext(), new SMSReaderBroadcastReceiver.OnTextMessageReceivedListener() {
            @Override
            public void onTextMessageReceive(String otp) {
                mOTPEditText.setText(otp);
                mActivateButton.performClick();
            }
        });
    }

    private void setCountDownTimer() {
        mResendOTPButton.setEnabled(false);
        new CustomCountDownTimer(SecuritySettingsActivity.otpDuration, 500) {

            public void onTick(long millisUntilFinished) {
                mResendOTPButton.setText(context.getString(R.string.resend) + " " + new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }

            public void onFinish() {
                mResendOTPButton.setEnabled(true);
            }
        }.start();
    }

    private void verifyInput() {
        boolean cancel = false;
        View focusView = null;

        mOTP = mOTPEditText.getText().toString().trim();

        String errorMessage = InputValidator.isValidOTP(context, mOTP);
        if (errorMessage != null) {
            mOTPEditText.setError(errorMessage);
            focusView = mOTPEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mOTP = mOTPEditText.getText().toString().trim();
            attemptDesiredRequestWithOTP();
        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(((Activity) context), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    private void attemptDesiredRequestWithOTP() {
        Gson gson = new Gson();
        if (mDesiredRequest.equals(Constants.COMMAND_PUT_TWO_FA_SETTING)) {
            mProgressDialog.setMessage(context.getString(R.string.change_two_fa_settings));
            mProgressDialog.show();
            TwoFaServiceListWithOTPRequest twoFaServiceListWithOTPRequest = gson.fromJson(json,
                    TwoFaServiceListWithOTPRequest.class);
            twoFaServiceListWithOTPRequest.setOtp(mOTP);
            json = gson.toJson(twoFaServiceListWithOTPRequest);
            mHttpPutAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_PUT_TWO_FA_SETTING, mUri, json, context);
            mHttpPutAsyncTask.mHttpResponseListener = this;
            mHttpPutAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (mDesiredRequest.equals(Constants.COMMAND_SEND_MONEY)) {
            mProgressDialog.setMessage(context.getString(R.string.progress_dialog_text_sending_money));
            mProgressDialog.show();
            SendMoneyRequest sendMoneyRequest = gson.fromJson(json, SendMoneyRequest.class);
            sendMoneyRequest.setOtp(mOTP);
            json = gson.toJson(sendMoneyRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (mDesiredRequest.equals(Constants.COMMAND_TOPUP_REQUEST)) {
            mProgressDialog.setMessage(context.getString(R.string.dialog_requesting_top_up));
            mProgressDialog.show();
            TopupRequest topupRequest = gson.fromJson(json, TopupRequest.class);
            topupRequest.setOtp(mOTP);
            json = gson.toJson(topupRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (mDesiredRequest.equals(Constants.COMMAND_ADD_MONEY)) {
            mProgressDialog.setMessage(context.getString(R.string.progress_dialog_add_money_in_progress));
            mProgressDialog.show();
            AddMoneyRequest addMoneyRequest = gson.fromJson(json, AddMoneyRequest.class);
            addMoneyRequest.setOtp(mOTP);
            json = gson.toJson(addMoneyRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_MONEY, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (mDesiredRequest.equals(Constants.COMMAND_WITHDRAW_MONEY)) {
            mProgressDialog.setMessage(context.getString(R.string.progress_dialog_withdraw_money_in_progress));
            mProgressDialog.show();
            WithdrawMoneyRequest withdrawMoneyRequest = gson.fromJson(json, WithdrawMoneyRequest.class);
            withdrawMoneyRequest.setOtp(mOTP);
            json = gson.toJson(withdrawMoneyRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_WITHDRAW_MONEY, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (mDesiredRequest.equals(Constants.COMMAND_PAYMENT)) {
            mProgressDialog.setMessage(context.getString(R.string.progress_dialog_text_payment));
            mProgressDialog.show();
            PaymentRequest paymentRequest = gson.fromJson(json, PaymentRequest.class);
            paymentRequest.setOtp(mOTP);
            json = gson.toJson(paymentRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        } else if (mDesiredRequest.equals(Constants.COMMAND_SET_PIN)) {
            mProgressDialog.setMessage(context.getString(R.string.saving_pin));
            mProgressDialog.show();
            SetPinRequest mSetPinRequest = gson.fromJson(json, SetPinRequest.class);
            mSetPinRequest.setOtp(mOTP);
            json = gson.toJson(mSetPinRequest);
            mHttpPutAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_SET_PIN, mUri, json, context);
            mHttpPutAsyncTask.mHttpResponseListener = this;
            mHttpPutAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (mDesiredRequest.equals(Constants.COMMAND_SEND_PAYMENT_REQUEST)) {
            mProgressDialog.setMessage(context.getString(R.string.progress_dialog_sending_payment_request));
            mProgressDialog.show();
            SendNewPaymentRequest mSendNewPaymentRequest = gson.fromJson(json, SendNewPaymentRequest.class);
            mSendNewPaymentRequest.setOtp(mOTP);
            json = gson.toJson(mSendNewPaymentRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_PAYMENT_REQUEST, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (mDesiredRequest.equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {
            mProgressDialog.setMessage(context.getString(R.string.accepting_send_money_request));
            mProgressDialog.show();
            RequestMoneyAcceptRejectOrCancelRequest requestMoneyAcceptRejectOrCancelRequest
                    = gson.fromJson(json, RequestMoneyAcceptRejectOrCancelRequest.class);
            requestMoneyAcceptRejectOrCancelRequest.setOtp(mOTP);
            json = gson.toJson(requestMoneyAcceptRejectOrCancelRequest);
            mHttpPostAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ACCEPT_REQUESTS_MONEY, mUri, json, context);
            mHttpPostAsyncTask.mHttpResponseListener = this;
            mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mHttpPutAsyncTask = null;
            if (context != null)
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        TwoFaServiceAccomplishWithOTPResponse twoFaServiceAccomplishWithOTPResponse
                = gson.fromJson(result.getJsonString(), TwoFaServiceAccomplishWithOTPResponse.class);
        if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
            if (context != null) {
                Utilities.hideKeyboard(context, view);
                ((MyApplication) context.getApplication()).launchLoginPage(twoFaServiceAccomplishWithOTPResponse.getMessage());
            }
        } else {
            try {
                if (result.getApiCommand().equals(Constants.COMMAND_PUT_TWO_FA_SETTING)) {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, twoFaServiceAccomplishWithOTPResponse.getMessage(), Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        mDismissListener.onDismissDialog();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, twoFaServiceAccomplishWithOTPResponse.getMessage(), Toast.LENGTH_LONG);
                        mOTPInputDialog.dismiss();
                    }

                } else if (result.getApiCommand().equals(Constants.COMMAND_SEND_MONEY)) {
                    SendMoneyResponse sendMoneyResponse = gson.fromJson(result.getJsonString(), SendMoneyResponse.class);
                    String message = sendMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_TOPUP_REQUEST)) {
                    TopupResponse topupResponse = gson.fromJson(result.getJsonString(), TopupResponse.class);
                    String message = topupResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_REQUEST_MONEY)) {
                    RequestMoneyResponse requestMoneyResponse = gson.fromJson(result.getJsonString(), RequestMoneyResponse.class);
                    String message = requestMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_MONEY)) {
                    AddMoneyResponse addMoneyResponse = gson.fromJson(result.getJsonString(), AddMoneyResponse.class);
                    String message = addMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_WITHDRAW_MONEY)) {
                    WithdrawMoneyResponse withdrawMoneyResponse = gson.fromJson(result.getJsonString(), WithdrawMoneyResponse.class);
                    String message = withdrawMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_SET_PIN)) {
                    SetPinResponse mSetPinResponse = gson.fromJson(result.getJsonString(), SetPinResponse.class);
                    String message = mSetPinResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_PAYMENT)) {
                    PaymentResponse paymentResponse = gson.fromJson(result.getJsonString(), PaymentResponse.class);
                    String message = paymentResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                } else if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {
                    RequestMoneyAcceptRejectOrCancelResponse requestMoneyAcceptRejectOrCancelResponse =
                            gson.fromJson(result.getJsonString(), RequestMoneyAcceptRejectOrCancelResponse.class);
                    String message = requestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_LONG);
                        mOTPInputDialog.dismiss();
                        mDismissListener.onDismissDialog();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_LONG);
                    }
                }

            } catch (Exception e) {
                Toaster.makeText(context, twoFaServiceAccomplishWithOTPResponse.getMessage(), Toast.LENGTH_LONG);
            }
        }
    }

    public interface dismissListener {
        void onDismissDialog();
    }
}