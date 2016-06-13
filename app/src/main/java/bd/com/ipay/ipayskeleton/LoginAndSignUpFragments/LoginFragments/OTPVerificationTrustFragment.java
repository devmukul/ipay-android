package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.EnableDisableReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.TextMessageReader;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationTrustFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

    private Button mActivateButton;
    private EditText mOTPEditText;
    private TextView mTimerTextView;

    private String mDeviceID;
    private ProgressDialog mProgressDialog;
    private SharedPreferences pref;

    private EnableDisableReceiver receiver;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_add_trusted_device);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification_trusted_device, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_logging_in));

        //enable broadcast receiver to get the text message to get the OTP
        receiver = new EnableDisableReceiver();
        receiver.enableBroadcastReceiver(getContext(), new TextMessageReader.OnTextMessageReceivedListener() {
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
                    attemptLogin(SignupOrLoginActivity.mMobileNumber, SignupOrLoginActivity.mPassword);

                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mTimerTextView.setVisibility(View.VISIBLE);
        new CountDownTimer(SignupOrLoginActivity.otpDuration, 1000) {

            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }

            public void onFinish() {
                mTimerTextView.setVisibility(View.INVISIBLE);
            }
        }.start();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utilities.showKeyboard(getActivity(), mOTPEditText);
    }

    @Override
    public void onDestroy() {
        receiver.disableBroadcastReceiver(getContext());
        super.onDestroy();
    }

    private void attemptLogin(String mUserNameLogin, String mPasswordLogin) {
        if (mLoginTask != null) {
            return;
        }

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

            String otp = mOTPEditText.getText().toString().trim();
            String pushRegistrationID = pref.getString(Constants.PUSH_NOTIFICATION_TOKEN, null);

            mProgressDialog.show();

            LoginRequest mLoginModel = new LoginRequest(mUserNameLogin, mPasswordLogin,
                    Constants.MOBILE_ANDROID + mDeviceID, null, otp, pushRegistrationID, null);
            Gson gson = new Gson();
            String json = gson.toJson(mLoginModel);
            mLoginTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_IN,
                    Constants.BASE_URL_MM + Constants.URL_LOGIN, json, getActivity());
            mLoginTask.mHttpResponseListener = this;
            mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mLoginTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_LOG_IN)) {


            try {
                mLoginResponseModel = gson.fromJson(result.getJsonString(), LoginResponse.class);
                String message = mLoginResponseModel.getMessage();

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    pref.edit().putBoolean(Constants.LOGGEDIN, true).commit();
                    pref.edit().putInt(Constants.ACCOUNT_TYPE, mLoginResponseModel.getAccountType()).commit();

                    if (mLoginResponseModel.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE)
                        pref.edit().putString(Constants.USERID, SignupOrLoginActivity.mMobileNumber).commit();
                    else
                        pref.edit().putString(Constants.USERID, SignupOrLoginActivity.mMobileNumberBusiness).commit();

                    ((SignupOrLoginActivity) getActivity()).switchToHomeActivity();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mLoginTask = null;
        }
    }
}

