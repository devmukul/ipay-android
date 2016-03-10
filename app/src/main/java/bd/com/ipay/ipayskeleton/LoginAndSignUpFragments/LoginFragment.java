package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ForgotPasswordActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPasswordRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword.ForgetPasswordResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LoginFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

    private HttpRequestPostAsyncTask mForgetPasswordTask = null;
    private ForgetPasswordResponse mForgetPasswordResponse;

    private EditText mUserNameLoginView;
    private EditText mPasswordLoginView;
    private Button mButtonLogin;
    private Button mButtonForgetPassword;
    private String mPasswordLogin;
    private String mUserNameLogin;

    private ProgressDialog mProgressDialog;
    private String mDeviceID;
    private SharedPreferences pref;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_login_page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        mButtonLogin = (Button) v.findViewById(R.id.login_button);
        mButtonForgetPassword = (Button) v.findViewById(R.id.forget_password_button);
        mUserNameLoginView = (EditText) v.findViewById(R.id.login_mobile_number);
        mPasswordLoginView = (EditText) v.findViewById(R.id.login_password);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptLogin();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mButtonForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPasswordDialogue();
            }
        });

        if (SignupOrLoginActivity.mMobileNumber != null) {
            // Delete +880 from the prefix
            String mobileNumberWithoutPrefix = ContactEngine.trimPrefix(
                    SignupOrLoginActivity.mMobileNumber);
            mUserNameLoginView.setText(mobileNumberWithoutPrefix);
        }

        return v;
    }

    private void forgetPasswordDialogue() {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.forgot_your_password)
                .customView(R.layout.dialog_forget_password_get_mobile_number, true)
                .positiveText(R.string.submit)
                .negativeText(R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        View view = dialog.getCustomView();
        final EditText mMobileNumberEditText = (EditText) view.findViewById(R.id.mobile_number);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                if (mMobileNumberEditText.getText().toString().trim().length() != 14) {
                    mMobileNumberEditText.setError(getString(R.string.error_invalid_mobile_number));
                    Toast.makeText(getActivity(), R.string.error_invalid_mobile_number, Toast.LENGTH_LONG).show();

                } else {
                    String mobileNumber = mMobileNumberEditText.getText().toString().trim();
                    if (Utilities.isConnectionAvailable(getActivity()))
                        attemptSendOTP(mobileNumber, Constants.MOBILE_ANDROID + mDeviceID);
                    else
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }
        });

    }

    private void attemptSendOTP(String mobileNumber, String deviceID) {
        if (mForgetPasswordTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.sending_otp));
        mProgressDialog.show();
        ForgetPasswordRequest mForgetPasswordRequest = new ForgetPasswordRequest(mobileNumber, deviceID);
        Gson gson = new Gson();
        String json = gson.toJson(mForgetPasswordRequest);
        mForgetPasswordTask = new HttpRequestPostAsyncTask(Constants.COMMAND_FORGET_PASSWORD_SEND_OTP,
                Constants.BASE_URL_POST_MM + Constants.URL_SEND_OTP_FORGET_PASSWORD, json, getActivity());

        // Save the mobile number and device id in a static field so that it can be used later in OTP verification fragment
        SignupOrLoginActivity.mMobileNumber = mobileNumber;

        mForgetPasswordTask.mHttpResponseListener = this;
        mForgetPasswordTask.execute((Void) null);

    }

    private void attemptLogin() {
        if (mLoginTask != null) {
            return;
        }

        // Reset errors.
        mPasswordLoginView.setError(null);

        // Store values at the time of the login attempt.
        mPasswordLogin = mPasswordLoginView.getText().toString().trim();
        mUserNameLogin = "+880" + mUserNameLoginView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPasswordLogin) && !isPasswordValid(mPasswordLogin)) {
            mPasswordLoginView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordLoginView;
            cancel = true;
        }

        if (mUserNameLoginView.getText().toString().trim().length() != 10) {
            mUserNameLoginView.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mUserNameLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            // Save user's login information while trying to login
            SignupOrLoginActivity.mMobileNumber = mUserNameLogin;
            SignupOrLoginActivity.mPassword = mPasswordLogin;
            SignupOrLoginActivity.mMobileNumberBusiness = mUserNameLogin;
            SignupOrLoginActivity.mPasswordBusiness = mPasswordLogin;

            mProgressDialog.setMessage(getString(R.string.progress_dialog_text_logging_in));
            mProgressDialog.show();

            String UUID = null;
            if (pref.contains(mUserNameLogin)) {
                UUID = pref.getString(mUserNameLogin, null);
            }

            // TODO: in otp verification personal business
            LoginRequest mLoginModel = new LoginRequest(mUserNameLogin, Utilities.md5(mPasswordLogin),
                    Constants.MOBILE_ANDROID + mDeviceID, UUID, null, null);
            Gson gson = new Gson();
            String json = gson.toJson(mLoginModel);
            mLoginTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_IN,
                    Constants.BASE_URL_POST_MM + Constants.URL_LOGIN, json, getActivity());
            mLoginTask.mHttpResponseListener = this;
            mLoginTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mLoginTask = null;
            mForgetPasswordTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_LOG_IN)) {
            try {
                if (resultList.size() > 2) {
                    mLoginResponseModel = gson.fromJson(resultList.get(2), LoginResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                        pref.edit().putBoolean(Constants.LOGGEDIN, true).commit();
                        pref.edit().putString(Constants.USERID, mUserNameLogin).commit();
                        pref.edit().putInt(Constants.ACCOUNT_TYPE, mLoginResponseModel.getAccountType()).commit();
                        ((SignupOrLoginActivity) getActivity()).switchToHomeActivity();

                    } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_ACCEPTED)) {
                        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                        pref.edit().putInt(Constants.ACCOUNT_TYPE, mLoginResponseModel.getAccountType()).commit();

                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mLoginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                        // First time login from this device. Verify OTP for secure login
                        ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationTrustedFragment();


                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mLoginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mLoginTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_FORGET_PASSWORD_SEND_OTP)) {
            try {
                if (resultList.size() > 2) {
                    mForgetPasswordResponse = gson.fromJson(resultList.get(2), ForgetPasswordResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                        startActivity(intent);

                    } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE)) {
                        Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                        startActivity(intent);

                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mForgetPasswordResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_request_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mForgetPasswordTask = null;
        }
    }
}
