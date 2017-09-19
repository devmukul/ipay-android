package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.Activity;
import android.app.ProgressDialog;
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
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFaServiceAccomplishWithOTPResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA.TwoFaServiceListWithOTPRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.CustomCountDownTimer;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
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

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    public OTPVerificationForTwoFaServicesDialog(@NonNull Activity context, String json, String desiredRequest, String mUri) {
        super(context);
        this.context = context;
        this.mDesiredRequest = desiredRequest;
        this.json = json;
        this.mUri = mUri;
        initializeView();
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
        mProgressDialog.setCancelable(true);

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

    private void attemptDesiredRequestWithOTP() {
        Gson gson = new Gson();
        if (mDesiredRequest.equals(Constants.COMMAND_PUT_TWO_FA_SETTING)) {
            mProgressDialog.setMessage(context.getString(R.string.change_password_progress));
            mProgressDialog.show();
            TwoFaServiceListWithOTPRequest twoFaServiceListWithOTPRequest = gson.fromJson(json,
                    TwoFaServiceListWithOTPRequest.class);
            twoFaServiceListWithOTPRequest.setOtp(mOTP);
            json = gson.toJson(twoFaServiceListWithOTPRequest);
            mHttpPutAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_PUT_TWO_FA_SETTING, mUri, json, context);
            mHttpPutAsyncTask.mHttpResponseListener = this;
            mHttpPutAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        try {
            if (result.getApiCommand().equals(Constants.COMMAND_PUT_TWO_FA_SETTING)) {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mProgressDialog.dismiss();
                    Toaster.makeText(context, twoFaServiceAccomplishWithOTPResponse.getMessage(), Toast.LENGTH_SHORT);
                    mOTPInputDialog.dismiss();
                    ((SecuritySettingsActivity) (context)).switchTo2FaSettingsFragment();
                } else {
                    Toaster.makeText(context, twoFaServiceAccomplishWithOTPResponse.getMessage(), Toast.LENGTH_LONG);
                }

            }

        } catch (Exception e) {
            Toaster.makeText(context,twoFaServiceAccomplishWithOTPResponse.getMessage(),Toast.LENGTH_LONG);
        }
    }
}