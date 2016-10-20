package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPRequestPersonalSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPResponsePersonalSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.SignupRequestPersonal;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.SignupResponsePersonal;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationPersonalFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSignUpTask = null;
    private SignupResponsePersonal mSignupResponseModel;

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponsePersonalSignup mOtpResponsePersonalSignup;

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

    private Button mActivateButton;
    private Button mResendOTPButton;
    private EditText mOTPEditText;
    private TextView mTimerTextView;

    private String mDeviceID;
    private ProgressDialog mProgressDialog;

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_personal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) v.findViewById(R.id.buttonResend);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);

        Utilities.showKeyboard(getActivity(), mOTPEditText);

        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_logging_in));

        //enable broadcast receiver to get the text message to get the OTP
        mEnableDisableSMSBroadcastReceiver = new EnableDisableSMSBroadcastReceiver();
        mEnableDisableSMSBroadcastReceiver.enableBroadcastReceiver(getContext(), new SMSReaderBroadcastReceiver.OnTextMessageReceivedListener() {
            @Override
            public void onTextMessageReceive(String otp) {
                mOTPEditText.setText(otp);
                mActivateButton.performClick();
            }
        });

        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SignupOrLoginActivity.mAccountType == Constants.PERSONAL_ACCOUNT_TYPE) {
                    if (Utilities.isConnectionAvailable(getActivity())) resendOTP();
                    else if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                } else {
                    ((SignupOrLoginActivity) getActivity()).switchToSignupPersonalStepOneFragment();
                }
            }
        });

        mActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                if (Utilities.isConnectionAvailable(getActivity())) attemptSignUp();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mResendOTPButton.setEnabled(false);
        mTimerTextView.setVisibility(View.VISIBLE);
        new CountDownTimer(SignupOrLoginActivity.otpDuration, 1000) {

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
        mEnableDisableSMSBroadcastReceiver.disableBroadcastReceiver(getContext());
        super.onDestroy();
    }

    private void resendOTP() {
        if (SignupOrLoginActivity.mAccountType == Constants.PERSONAL_ACCOUNT_TYPE) {

            if (mRequestOTPTask != null) {
                return;
            }

            mProgressDialog.show();

            OTPRequestPersonalSignup mOtpRequestPersonalSignup = new OTPRequestPersonalSignup(SignupOrLoginActivity.mMobileNumber,
                    Constants.MOBILE_ANDROID + mDeviceID, Constants.PERSONAL_ACCOUNT_TYPE, SignupOrLoginActivity.mPromoCode);
            Gson gson = new Gson();
            String json = gson.toJson(mOtpRequestPersonalSignup);
            mRequestOTPTask = new
                    HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                    Constants.BASE_URL_MM + Constants.URL_OTP_REQUEST, json, getActivity()

            );
            mRequestOTPTask.mHttpResponseListener = this;
            mRequestOTPTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void attemptSignUp() {
        if (mSignUpTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String otp = mOTPEditText.getText().toString().trim();

        if (otp.length() == 0) {
            mOTPEditText.setError(getActivity().getString(R.string.error_invalid_otp));
            focusView = mOTPEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            mProgressDialog.show();
            SignupRequestPersonal mSignupModel = new SignupRequestPersonal(SignupOrLoginActivity.mMobileNumber,
                    Constants.MOBILE_ANDROID + mDeviceID,
                    SignupOrLoginActivity.mName,
                    SignupOrLoginActivity.mBirthday, SignupOrLoginActivity.mPassword, otp, SignupOrLoginActivity.mPromoCode,
                    Constants.PERSONAL_ACCOUNT_TYPE, SignupOrLoginActivity.mAddressPersonal);
            Gson gson = new Gson();
            String json = gson.toJson(mSignupModel);
            mSignUpTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SIGN_UP,
                    Constants.BASE_URL_MM + Constants.URL_SIGN_UP, json, getActivity());
            mSignUpTask.mHttpResponseListener = this;
            mSignUpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    private void attemptLogin(String mUserNameLogin, String mPasswordLogin, String otp) {
        if (mLoginTask != null) {
            return;
        }

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSignUpTask = null;
            mRequestOTPTask = null;
            mLoginTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_SIGN_UP:
                try {
                    mSignupResponseModel = gson.fromJson(result.getJsonString(), SignupResponsePersonal.class);
                    String message = mSignupResponseModel.getMessage();
                    String otp = mSignupResponseModel.getOtp();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                        pref.edit().putString(Constants.USERID, SignupOrLoginActivity.mMobileNumber).apply();
                        pref.edit().putString(Constants.PASSWORD, SignupOrLoginActivity.mPassword).apply();
                        pref.edit().putString(Constants.NAME, SignupOrLoginActivity.mName).apply();
                        pref.edit().putString(Constants.BIRTHDAY, SignupOrLoginActivity.mBirthday).apply();
                        pref.edit().putString(Constants.GENDER, SignupOrLoginActivity.mGender).apply();
                        pref.edit().putString(Constants.USERCOUNTRY, "Bangladesh").apply();   // TODO
                        pref.edit().putInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE).apply();

                        // Request a login immediately after sign up
                        if (Utilities.isConnectionAvailable(getActivity()))
                            attemptLogin(SignupOrLoginActivity.mMobileNumber, SignupOrLoginActivity.mPassword, otp);

                        // TODO: For now, switch to login fragment after a successful sign up. Don't remove it either. Can be used later
//                        ((SignupOrLoginActivity) getActivity()).switchToLoginFragment();

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
                mSignUpTask = null;

                break;
            case Constants.COMMAND_OTP_VERIFICATION:
                try {
                    mOtpResponsePersonalSignup = gson.fromJson(result.getJsonString(), OTPResponsePersonalSignup.class);
                    String message = mOtpResponsePersonalSignup.getMessage();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.otp_sent, Toast.LENGTH_LONG).show();

                        // Start timer again
                        mTimerTextView.setVisibility(View.VISIBLE);
                        mResendOTPButton.setEnabled(false);
                        new CountDownTimer(SignupOrLoginActivity.otpDuration, 1000) {

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
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mRequestOTPTask = null;

                break;
            case Constants.COMMAND_LOG_IN:
                try {
                    mLoginResponseModel = gson.fromJson(result.getJsonString(), LoginResponse.class);
                    String message = mLoginResponseModel.getMessage();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                        pref.edit().putBoolean(Constants.LOGGED_IN, true).apply();
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
                break;
        }
    }
}

