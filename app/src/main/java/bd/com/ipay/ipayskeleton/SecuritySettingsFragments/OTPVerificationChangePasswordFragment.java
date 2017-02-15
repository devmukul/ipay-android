package bd.com.ipay.ipayskeleton.SecuritySettingsFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.ChangePasswordRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.ChangePasswordResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.ChangePasswordValidationRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials.ChangePasswordValidationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationChangePasswordFragment extends Fragment implements HttpResponseListener {
    private HttpRequestPutAsyncTask mChangePasswordTask = null;
    private ChangePasswordResponse mChangePasswordResponse;

    private HttpRequestPutAsyncTask mRequestOTPTask = null;
    private ChangePasswordValidationResponse mChangePasswordValidationResponse;

    private Button mActivateButton;
    private EditText mOTPEditText;
    private TextView mTimerTextView;
    private Button mResendOTPButton;

    private ProgressDialog mProgressDialog;

    private String mPassword;
    private String mNewPassword;
    private String mOTP;

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification_trusted_device, container, false);

        Bundle bundle = getArguments();

        mPassword = bundle.getString(Constants.PASSWORD);
        mNewPassword = bundle.getString(Constants.NEW_PASSWORD);

        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) v.findViewById(R.id.buttonResend);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.change_password_progress));
        mProgressDialog.setCancelable(true);

        //enable broadcast receiver to get the text message to get the OTP
        mEnableDisableSMSBroadcastReceiver = new EnableDisableSMSBroadcastReceiver();

        mEnableDisableSMSBroadcastReceiver.enableBroadcastReceiver(getContext(), new SMSReaderBroadcastReceiver.OnTextMessageReceivedListener() {
            @Override
            public void onTextMessageReceive(String otp) {
                mOTPEditText.setText(otp);
                mActivateButton.performClick();
            }
        });

        mActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiding the keyboard after verifying OTP
                Utilities.hideKeyboard(getActivity());
                if (Utilities.isConnectionAvailable(getActivity())) {
                    verifyInput();

                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity()))
                    resendOTP();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

            }
        });

        mResendOTPButton.setEnabled(false);
        mTimerTextView.setVisibility(View.VISIBLE);
        new CountDownTimer(SecuritySettingsActivity.otpDuration, 1000 - 500) {

            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }

            public void onFinish() {

                //mTimerTextView.setVisibility(View.INVISIBLE);
                mResendOTPButton.setEnabled(true);
            }
        }.start();

        if (Constants.DEBUG && Constants.AUTO_LOGIN && (Constants.SERVER_TYPE == 1 || Constants.SERVER_TYPE == 2)) {
            mOTPEditText.setText("123456");
            mActivateButton.callOnClick();
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_change_password);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utilities.showKeyboard(getActivity(), mOTPEditText);
    }

    @Override
    public void onDestroy() {
        mEnableDisableSMSBroadcastReceiver.disableBroadcastReceiver(getContext());
        super.onDestroy();
    }

    private void resendOTP() {
        if (mRequestOTPTask != null) {
            return;
        }

        mProgressDialog.show();

        ChangePasswordValidationRequest mChangePasswordRequest = new ChangePasswordValidationRequest(mPassword, mNewPassword);
        Gson gson = new Gson();
        String json = gson.toJson(mChangePasswordRequest);
        mRequestOTPTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CHANGE_PASSWORD,
                Constants.BASE_URL_MM + Constants.URL_CHANGE_PASSWORD, json, getActivity());
        mChangePasswordTask.mHttpResponseListener = this;
        mChangePasswordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void verifyInput() {
        boolean cancel = false;
        View focusView = null;

        if (mOTPEditText.getText().toString().trim().length() == 0) {
            mOTPEditText.setError(getString(R.string.error_invalid_otp));
            focusView = mOTPEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            mOTP = mOTPEditText.getText().toString().trim();
            attemptChangePassword();
        }
    }

    private void attemptChangePassword() {

        if (mChangePasswordTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.change_password_progress));
        mProgressDialog.show();
        ChangePasswordRequest mChangePasswordRequest = new ChangePasswordRequest(mPassword, mNewPassword, mOTP);
        Gson gson = new Gson();
        String json = gson.toJson(mChangePasswordRequest);
        mChangePasswordTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CHANGE_PASSWORD,
                Constants.BASE_URL_MM + Constants.URL_CHANGE_PASSWORD, json, getActivity());
        mChangePasswordTask.mHttpResponseListener = this;
        mChangePasswordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (isAdded()) mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mChangePasswordTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_CHANGE_PASSWORD)) {

            try {
                mChangePasswordResponse = gson.fromJson(result.getJsonString(), ChangePasswordResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                    Utilities.hideKeyboard(getActivity());
                    ((SecuritySettingsActivity) getActivity()).switchToAccountSettingsFragment();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.change_pass_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mChangePasswordTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_OTP_VERIFICATION)) {

            try {
                mChangePasswordValidationResponse = gson.fromJson(result.getJsonString(), ChangePasswordValidationResponse.class);
                SecuritySettingsActivity.otpDuration = mChangePasswordValidationResponse.getOtpValidFor();
                String message = mChangePasswordValidationResponse.getMessage();

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.otp_sent, Toast.LENGTH_LONG).show();

                    // Start timer again
                    mTimerTextView.setVisibility(View.VISIBLE);
                    mResendOTPButton.setEnabled(false);
                    new CountDownTimer(SecuritySettingsActivity.otpDuration, 1000) {

                        public void onTick(long millisUntilFinished) {
                            mTimerTextView.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
                        }

                        public void onFinish() {
                            mTimerTextView.setVisibility(View.INVISIBLE);
                            mResendOTPButton.setEnabled(true);
                        }
                    }.start();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mRequestOTPTask = null;
        }
    }
}


