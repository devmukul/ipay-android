package bd.com.ipay.ipayskeleton.ForgotPasswordFragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPassOTPConfirmationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPassOTPConfirmationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.TrustedOtp;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.TrustedOtpReceiver;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationForgotPasswordFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mOTPConfirmationTask = null;
    private ForgetPassOTPConfirmationResponse mForgetPassOTPConfirmationResponse;

    private Button mActivateButton;
    private Button mResendOTPButton;
    private EditText mOTPEditText;
    private EditText mNewPasswordEditText;
    private TextView mOtpSentInfo;
    private TextView mTimerTextView;
    private LinearLayout mTrustedOtpReceiverLayout;
    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    private String mDeviceID;
    private List<TrustedOtpReceiver> mTrustedOtpReceivers;
    private ProgressDialog mProgressDialog;

    private ArrayList<EditText> mTrustedOtpReceiverEditTexts;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_personal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification_forget_password, container, false);
        mOtpSentInfo = (TextView) v.findViewById(R.id.otp_sent_info);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) v.findViewById(R.id.buttonResend);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);
        mNewPasswordEditText = (EditText) v.findViewById(R.id.new_pass_edittext);
        mTrustedOtpReceiverLayout = (LinearLayout) v.findViewById(R.id.trusted_people_otp_containers);

        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());
        mTrustedOtpReceivers = getActivity().getIntent().getParcelableArrayListExtra(Constants.TRUSTED_OTP_RECEIVERS);

        List<String> trustedOtpReceiverInfos = new ArrayList<>();
        for (TrustedOtpReceiver trustedOtpReceiver : mTrustedOtpReceivers) {
            trustedOtpReceiverInfos.add(trustedOtpReceiver.getName() + " (" + trustedOtpReceiver.getMobileNumber() + ")");
        }
        mOtpSentInfo.setText(getString(R.string.otp_has_been_sent_to_your_mobile_number)
                + "(" + SignupOrLoginActivity.mMobileNumber + ")"
                + " and the following numbers: "
                + StringUtils.join(trustedOtpReceiverInfos, ", "));

        mTrustedOtpReceiverEditTexts = new ArrayList<>();
        for (TrustedOtpReceiver trustedOtpReceiver : mTrustedOtpReceivers) {
            TextInputLayout textInputLayout = new TextInputLayout(getActivity());
            textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            EditText trustedOtpReceiverEditText = new EditText(getActivity());
            trustedOtpReceiverEditText.setGravity(Gravity.CENTER);
            trustedOtpReceiverEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            trustedOtpReceiverEditText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            trustedOtpReceiverEditText.setHint("Enter OTP sent to " + trustedOtpReceiver.getName());
            mTrustedOtpReceiverEditTexts.add(trustedOtpReceiverEditText);

            textInputLayout.addView(trustedOtpReceiverEditText);
            mTrustedOtpReceiverLayout.addView(textInputLayout);
        }

        mProgressDialog = new ProgressDialog(getActivity());

        //enable broadcast receiver to get the text message to get the OTP
        mEnableDisableSMSBroadcastReceiver = new EnableDisableSMSBroadcastReceiver();
        mEnableDisableSMSBroadcastReceiver.enableBroadcastReceiver(getActivity(), new SMSReaderBroadcastReceiver.OnTextMessageReceivedListener() {
            @Override
            public void onTextMessageReceive(String otp) {
                mOTPEditText.setText(otp);
                mActivateButton.performClick();
            }
        });

        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
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

    @Override
    public void onDestroy() {
        mEnableDisableSMSBroadcastReceiver.disableBroadcastReceiver(getActivity());
        super.onDestroy();
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

        String passwordValidationMsg = InputValidator.isPasswordValid(mNewPasswordEditText.getText().toString().trim());
        if (passwordValidationMsg.length() > 0) {
            mNewPasswordEditText.setError(passwordValidationMsg);
            focusView = mNewPasswordEditText;
            cancel = true;
        }

        for (int i = 0; i < mTrustedOtpReceivers.size() && i < mTrustedOtpReceiverEditTexts.size(); i++) {
            EditText trustedOtpReceiverEditText = mTrustedOtpReceiverEditTexts.get(i);
            if (trustedOtpReceiverEditText.getText().toString().trim().isEmpty()) {
                trustedOtpReceiverEditText.setError(getString(R.string.invalid_otp));
                focusView = trustedOtpReceiverEditText;
                cancel = true;
            }
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

            List<TrustedOtp> trustedOtps = new ArrayList<>();
            for (int i = 0; i < mTrustedOtpReceivers.size() && i < mTrustedOtpReceiverEditTexts.size(); i++) {
                trustedOtps.add(new TrustedOtp(mTrustedOtpReceivers.get(i).getPersonId(), mTrustedOtpReceiverEditTexts.get(i).getText().toString().trim()));
            }

            ForgetPassOTPConfirmationRequest mForgetPassOTPConfirmationRequest = new ForgetPassOTPConfirmationRequest
                    (SignupOrLoginActivity.mMobileNumber, Constants.MOBILE_ANDROID + mDeviceID, otp, newPassword, trustedOtps);
            Gson gson = new Gson();
            String json = gson.toJson(mForgetPassOTPConfirmationRequest);

            mOTPConfirmationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_FORGET_PASSWORD_CONFIRM_OTP,
                    Constants.BASE_URL_MM + Constants.URL_CONFIRM_OTP_FORGET_PASSWORD, json, getActivity());
            mOTPConfirmationTask.mHttpResponseListener = this;
            mOTPConfirmationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mOTPConfirmationTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_FORGET_PASSWORD_CONFIRM_OTP)) {

            try {
                mForgetPassOTPConfirmationResponse = gson.fromJson(result.getJsonString(), ForgetPassOTPConfirmationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mForgetPassOTPConfirmationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    getActivity().finish();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mForgetPassOTPConfirmationResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_to_reset_password, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mOTPConfirmationTask = null;
        }
    }
}

