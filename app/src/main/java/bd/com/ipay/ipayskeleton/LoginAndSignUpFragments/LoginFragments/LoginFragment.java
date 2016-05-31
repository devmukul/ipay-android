package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LoginFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

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

        putConstantStringInfront(mUserNameLoginView, "+880");

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hiding the keyboard after login button pressed
                Utilities.hideKeyboard(getActivity());

                if (Utilities.isConnectionAvailable(getActivity())) attemptLogin();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mButtonForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SignupOrLoginActivity) getActivity()).switchToForgetPasswordFragment();
            }
        });

        if (SignupOrLoginActivity.mMobileNumber != null) {
            // Delete +880 from the prefix
            String mobileNumberWithoutPrefix = ContactEngine.trimPrefix(
                    SignupOrLoginActivity.mMobileNumber);
            mUserNameLoginView.setText(Constants.COUNTRY_CODE_BANGLADESH + mobileNumberWithoutPrefix);
        } else if (pref.contains(Constants.USERID)) {
            String userIdWithoutPrefix = ContactEngine.trimPrefix(pref.getString(Constants.USERID, ""));
            mUserNameLoginView.setText(Constants.COUNTRY_CODE_BANGLADESH + userIdWithoutPrefix);
        }

        // Auto Login
        if (pref.contains(Constants.USERID) && Constants.DEBUG && Constants.AUTO_LOGIN) {
            mPasswordLoginView.setText("qqqqqqq1");
            //           mUserNameLoginView.setText("+8801677258077");
            attemptLogin();
        }

        return v;
    }

    void putConstantStringInfront(final EditText edt, final String constString) {
        edt.setText(constString);
        Selection.setSelection(edt.getText(), edt.getText().length());


        edt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith(constString)) {
                    edt.setText(constString);
                    Selection.setSelection(edt.getText(), edt.getText().length());

                }

            }
        });
    }

    private void attemptLogin() {
        if (mLoginTask != null) {
            return;
        }

        // Reset errors.
        mPasswordLoginView.setError(null);

        // Store values at the time of the login attempt.
        mPasswordLogin = mPasswordLoginView.getText().toString().trim();
        mUserNameLogin = mUserNameLoginView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        String passwordValidationMsg = Utilities.isPasswordValid(mPasswordLogin);
        if (passwordValidationMsg.length() > 0) {
            mPasswordLoginView.setError(passwordValidationMsg);
            focusView = mPasswordLoginView;
            cancel = true;
        }

        if (mUserNameLoginView.getText().toString().trim().length() != 14) {
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
            if (pref.contains(Constants.UUID)) {
                UUID = pref.getString(Constants.UUID, null);
            }

            String pushRegistrationID = pref.getString(Constants.PUSH_NOTIFICATION_TOKEN, null);

            LoginRequest mLoginModel = new LoginRequest(mUserNameLogin, mPasswordLogin,
                    Constants.MOBILE_ANDROID + mDeviceID, UUID, null, pushRegistrationID, null);
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

//        Log.w("Result", result);
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

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                    pref.edit().putBoolean(Constants.LOGGEDIN, true).commit();
                    pref.edit().putString(Constants.USERID, mUserNameLogin).commit();
                    pref.edit().putInt(Constants.ACCOUNT_TYPE, mLoginResponseModel.getAccountType()).commit();
                    mProgressDialog.dismiss();
                    ((SignupOrLoginActivity) getActivity()).switchToHomeActivity();

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
//                        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
//                        pref.edit().putInt(Constants.ACCOUNT_TYPE, mLoginResponseModel.getAccountType()).commit();

                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mLoginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    // First time login from this device. Verify OTP for secure login
                    SignupOrLoginActivity.otpDuration = mLoginResponseModel.getOtpValidFor();
                    mProgressDialog.dismiss();
                    ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationTrustedFragment();

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE) {

                    // OTP has not been expired yet
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mLoginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();

                    // Enter previous OTP
                    SignupOrLoginActivity.otpDuration = mLoginResponseModel.getOtpValidFor();
                    mProgressDialog.dismiss();

                    ((SignupOrLoginActivity) getActivity()).switchToOTPVerificationTrustedFragment();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mLoginResponseModel.getMessage(), Toast.LENGTH_SHORT).show();
                }


            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mLoginTask = null;

        }
    }
}
