package bd.com.ipay.ipayskeleton.ForgotPasswordFragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import bd.com.ipay.ipayskeleton.Activities.ForgotPasswordActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPassOTPConfirmationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPassOTPConfirmationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationForgotPasswordFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mOTPConfirmationTask = null;
    private ForgetPassOTPConfirmationResponse mForgetPassOTPConfirmationResponse;

    private Button mActivateButton;
    private Button mResendOTPButton;
    private EditText mOTPEditText;
    private EditText mNewPasswordEditText;
    private TextView mTimerTextView;

    private String mDeviceID;
    private ProgressDialog mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_personal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification_forget_password, container, false);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) v.findViewById(R.id.buttonResend);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);
        mNewPasswordEditText = (EditText) v.findViewById(R.id.new_pass_edittext);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        mProgressDialog = new ProgressDialog(getActivity());

        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ForgotPasswordActivity) getActivity()).finish();
            }
        });

        mActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptOTPConfirmation();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mResendOTPButton.setEnabled(false);
        mTimerTextView.setVisibility(View.VISIBLE);
        new CountDownTimer(1800000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
            }

            public void onFinish() {
                mTimerTextView.setVisibility(View.INVISIBLE);
                mResendOTPButton.setEnabled(true);
            }
        }.start();

        return v;
    }


    private void attemptOTPConfirmation() {
        if (mOTPConfirmationTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (mOTPEditText.getText().toString().trim().length() == 0) {
            mOTPEditText.setError(getString(R.string.invalid_otp));
            focusView = mOTPEditText;
            cancel = true;
        }

        String passwordValidationMsg = Utilities.isPasswordValid(mNewPasswordEditText.getText().toString().trim());
        if (passwordValidationMsg.length() > 0) {
            mNewPasswordEditText.setError(passwordValidationMsg);
            focusView = mNewPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            mProgressDialog.setMessage(getString(R.string.progress_dialog_saving_new_pass));
            mProgressDialog.show();
            String otp = mOTPEditText.getText().toString().trim();
            String newPassword = mNewPasswordEditText.getText().toString().trim();

            ForgetPassOTPConfirmationRequest mForgetPassOTPConfirmationRequest = new ForgetPassOTPConfirmationRequest
                    (SignupOrLoginActivity.mMobileNumber, Constants.MOBILE_ANDROID + mDeviceID, otp, newPassword);
            Gson gson = new Gson();
            String json = gson.toJson(mForgetPassOTPConfirmationRequest);
            mOTPConfirmationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_FORGET_PASSWORD_CONFIRM_OTP,
                    Constants.BASE_URL_POST_MM + Constants.URL_CONFIRM_OTP_FORGET_PASSWORD, json, getActivity());
            mOTPConfirmationTask.mHttpResponseListener = this;
            mOTPConfirmationTask.execute((Void) null);
        }
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mOTPConfirmationTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_FORGET_PASSWORD_CONFIRM_OTP)) {

            if (resultList.size() > 2) {

                try {
                    mForgetPassOTPConfirmationResponse = gson.fromJson(resultList.get(2), ForgetPassOTPConfirmationResponse.class);
                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        ((ForgotPasswordActivity) getActivity()).finish();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mForgetPassOTPConfirmationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_to_reset_password, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_to_reset_password, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mOTPConfirmationTask = null;

        }
    }
}

