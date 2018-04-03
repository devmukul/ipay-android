package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.Activity;
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
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFactorAuthSettingsSaveResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.CustomCountDownTimer;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthServicesAsynctaskMap;
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
    //private ProgressDialog mProgressDialog;
    private CustomProgressDialog mCustomProgressDialog;

    public HttpResponseListener mParentHttpResponseListener;

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
        mProgressDialogStringMap = TwoFactorAuthConstants.getProgressDialogStringMap(context);
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

        mCustomProgressDialog = new CustomProgressDialog(context);

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
                mCustomProgressDialog.setLoadingMessage(mProgressDialogStringMap.get(desiredRequest));
                mCustomProgressDialog.showDialog();
                hideOtpDialog();
                mHttpPutAsyncTask = TwoFactorAuthServicesAsynctaskMap.getPutAsyncTask(desiredRequest, json, otp, context, mUri);
                mHttpPutAsyncTask.mHttpResponseListener = this;
                mHttpPutAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (method.equals(Constants.METHOD_POST)) {
            if (mHttpPostAsyncTask != null) return;
            else {
                mCustomProgressDialog.setLoadingMessage(mProgressDialogStringMap.get(desiredRequest));
                mCustomProgressDialog.showDialog();
                hideOtpDialog();
                mHttpPostAsyncTask = TwoFactorAuthServicesAsynctaskMap.getPostAsyncTask(desiredRequest, json, otp, context, mUri);
                mHttpPostAsyncTask.mHttpResponseListener = this;
                mHttpPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    public void hideOtpDialog() {
        view.setVisibility(View.GONE);
    }

    public void showOtpDialog() {
        view.setVisibility(View.VISIBLE);
    }

    public void dismissDialog() {
        mOTPInputDialog.dismiss();
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mHttpPutAsyncTask = null;
            mHttpPostAsyncTask = null;
            if (context != null) {
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            }
            mCustomProgressDialog.dismissDialog();
            return;
        } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
            TwoFactorAuthSettingsSaveResponse twoFactorAuthSettingsSaveResponse =
                    new Gson().fromJson(result.getJsonString(), TwoFactorAuthSettingsSaveResponse.class);
            mCustomProgressDialog.showSuccessAnimationAndMessage(twoFactorAuthSettingsSaveResponse.getMessage());
        }
        else{
            mCustomProgressDialog.dismissDialog();
        }
        Gson gson = new Gson();
        TwoFactorAuthSettingsSaveResponse twoFactorAuthSettingsSaveResponse
                = gson.fromJson(result.getJsonString(), TwoFactorAuthSettingsSaveResponse.class);
        mHttpPutAsyncTask = null;
        mHttpPostAsyncTask = null;
        mParentHttpResponseListener.httpResponseReceiver(result);
        return;
    }

}