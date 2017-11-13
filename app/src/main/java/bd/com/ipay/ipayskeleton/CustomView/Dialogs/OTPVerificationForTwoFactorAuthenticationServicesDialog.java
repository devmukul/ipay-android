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
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyAcceptRejectOrCancelResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFaSettingsSaveResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.CustomCountDownTimer;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TwoFaServicesAsyncTaskMap;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationForTwoFactorAuthenticationServicesDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private Activity context;

    private static String desiredRequest;

    private HttpRequestPostAsyncTask mHttpPostAsyncTask;

    private HttpRequestPutAsyncTask mHttpPutAsyncTask;

    private String json;
    private String mOTP;
    private String mUri;
    private String method;
    private EditText mOTPEditText;
    private Button mActivateButton;
    private Button mCancelButton;
    private Button mResendOTPButton;
    private View view;

    private MaterialDialog mOTPInputDialog;
    private ProgressDialog mProgressDialog;

    public dismissListener mDismissListener;

    private HashMap<String, String> mProgressDialogStringMap;

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    public OTPVerificationForTwoFactorAuthenticationServicesDialog(@NonNull Activity context, String json, String desiredRequest, String mUri, String method) {
        super(context);
        this.context = context;
        this.desiredRequest = desiredRequest;
        this.json = json;
        this.mUri = mUri;
        this.method = method;
        initializeView();
        createProgresDialogStringMap();
    }

    private void createProgresDialogStringMap() {
        mProgressDialogStringMap = new HashMap<>();
        mProgressDialogStringMap.put(Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS, context.getString(R.string.change_two_fa_settings));
        mProgressDialogStringMap.put(Constants.COMMAND_SEND_MONEY, context.getString(R.string.progress_dialog_text_sending_money));
        mProgressDialogStringMap.put(Constants.COMMAND_TOPUP_REQUEST, context.getString(R.string.dialog_requesting_top_up));
        mProgressDialogStringMap.put(Constants.COMMAND_ADD_MONEY, context.getString(R.string.progress_dialog_add_money_in_progress));
        mProgressDialogStringMap.put(Constants.COMMAND_WITHDRAW_MONEY, context.getString(R.string.progress_dialog_withdraw_money_in_progress));
        mProgressDialogStringMap.put(Constants.COMMAND_PAYMENT, context.getString(R.string.progress_dialog_text_payment));
        mProgressDialogStringMap.put(Constants.COMMAND_SET_PIN, context.getString(R.string.saving_pin));
        mProgressDialogStringMap.put(Constants.COMMAND_SEND_PAYMENT_REQUEST, context.getString(R.string.progress_dialog_sending_payment_request));
        mProgressDialogStringMap.put(Constants.COMMAND_ACCEPT_REQUESTS_MONEY, context.getString(R.string.accepting_send_money_request));
    }

    public OTPVerificationForTwoFactorAuthenticationServicesDialog(Activity context) {
        super(context);

    }

    public void initializeView() {
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

    public void dismiss() {
        this.dismiss();
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
                mEnableDisableSMSBroadcastReceiver.disableBroadcastReceiver(getContext());
                mOTPInputDialog.dismiss();

            }
        });
        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(context))
                    attemptDesiredRequestWithOTP(null);
                else
                    Toaster.makeText(context, R.string.no_internet_connection, Toast.LENGTH_LONG);
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
            focusView.requestFocus();
        } else {
            mOTP = mOTPEditText.getText().toString().trim();
            attemptDesiredRequestWithOTP(mOTP);
        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(((Activity) context), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    private void attemptDesiredRequestWithOTP(String otp) {
        if (method.equals(Constants.METHOD_PUT)) {
            if (mHttpPutAsyncTask != null) return;
            else {
                mProgressDialog.setMessage(mProgressDialogStringMap.get(desiredRequest));
                mProgressDialog.show();
                mHttpPutAsyncTask = TwoFaServicesAsyncTaskMap.getPutAsyncTask(desiredRequest, json, otp, context, mUri);
                mHttpPutAsyncTask.mHttpResponseListener = this;
                mHttpPutAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (method.equals(Constants.METHOD_POST)) {
            if (mHttpPostAsyncTask != null) return;
            else {
                mProgressDialog.setMessage(mProgressDialogStringMap.get(desiredRequest));
                mProgressDialog.show();
                mHttpPostAsyncTask = TwoFaServicesAsyncTaskMap.getPostAsyncTask(desiredRequest, json, otp, context, mUri);
                mHttpPostAsyncTask.mHttpResponseListener = this;
                mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
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

        TwoFaSettingsSaveResponse twoFaSettingsSaveResponse
                = gson.fromJson(result.getJsonString(), TwoFaSettingsSaveResponse.class);
        if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
            if (context != null) {
                Utilities.hideKeyboard(context, view);
                ((MyApplication) context.getApplication()).launchLoginPage(twoFaSettingsSaveResponse.getMessage());
            }
        } else {
            try {
                if (result.getApiCommand().equals(Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS)) {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, twoFaSettingsSaveResponse.getMessage(), Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        mDismissListener.onDismissDialog();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, twoFaSettingsSaveResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = twoFaSettingsSaveResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, twoFaSettingsSaveResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = twoFaSettingsSaveResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, twoFaSettingsSaveResponse.getMessage(), Toast.LENGTH_LONG);
                        mOTPInputDialog.hide();
                    }
                    mHttpPutAsyncTask = null;

                } else if (result.getApiCommand().equals(Constants.COMMAND_SEND_MONEY)) {
                    SendMoneyResponse sendMoneyResponse = gson.fromJson(result.getJsonString(), SendMoneyResponse.class);
                    String message = sendMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mEnableDisableSMSBroadcastReceiver.disableBroadcastReceiver(context);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, sendMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = sendMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, sendMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = sendMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPostAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_TOPUP_REQUEST)) {
                    TopupResponse topupResponse = gson.fromJson(result.getJsonString(), TopupResponse.class);
                    String message = topupResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, topupResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = topupResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, topupResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = topupResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPostAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_REQUEST_MONEY)) {
                    RequestMoneyResponse requestMoneyResponse = gson.fromJson(result.getJsonString(), RequestMoneyResponse.class);
                    String message = requestMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, requestMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = requestMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, requestMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = requestMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPostAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_ADD_MONEY)) {
                    AddMoneyResponse addMoneyResponse = gson.fromJson(result.getJsonString(), AddMoneyResponse.class);
                    String message = addMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, addMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = addMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, addMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = addMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPostAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_WITHDRAW_MONEY)) {
                    WithdrawMoneyResponse withdrawMoneyResponse = gson.fromJson(result.getJsonString(), WithdrawMoneyResponse.class);
                    String message = withdrawMoneyResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, withdrawMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = withdrawMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, withdrawMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = withdrawMoneyResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPostAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_SET_PIN)) {
                    SetPinResponse mSetPinResponse = gson.fromJson(result.getJsonString(), SetPinResponse.class);
                    String message = mSetPinResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, mSetPinResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = mSetPinResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, mSetPinResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = mSetPinResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPutAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_PAYMENT)) {
                    PaymentResponse paymentResponse = gson.fromJson(result.getJsonString(), PaymentResponse.class);
                    String message = paymentResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                        mOTPInputDialog.dismiss();
                        launchHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, paymentResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = paymentResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, paymentResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = paymentResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_SHORT);
                    }
                    mHttpPostAsyncTask = null;
                } else if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_REQUESTS_MONEY)) {
                    RequestMoneyAcceptRejectOrCancelResponse requestMoneyAcceptRejectOrCancelResponse =
                            gson.fromJson(result.getJsonString(), RequestMoneyAcceptRejectOrCancelResponse.class);
                    String message = requestMoneyAcceptRejectOrCancelResponse.getMessage();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_LONG);
                        mOTPInputDialog.dismiss();
                        mDismissListener.onDismissDialog();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, requestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = requestMoneyAcceptRejectOrCancelResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, requestMoneyAcceptRejectOrCancelResponse.getMessage(), Toast.LENGTH_LONG);
                        SecuritySettingsActivity.otpDuration = requestMoneyAcceptRejectOrCancelResponse.getOtpValidFor();
                        setCountDownTimer();
                    } else {
                        mProgressDialog.dismiss();
                        Toaster.makeText(context, message, Toast.LENGTH_LONG);
                    }
                    mHttpPostAsyncTask = null;
                }

            } catch (Exception e) {
                Toaster.makeText(context, twoFaSettingsSaveResponse.getMessage(), Toast.LENGTH_LONG);
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        }
    }

    public interface dismissListener {
        void onDismissDialog();
    }
}