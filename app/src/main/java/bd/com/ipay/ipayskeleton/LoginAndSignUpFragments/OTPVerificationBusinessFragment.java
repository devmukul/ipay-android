package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.FireBase.UserPersonal;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.SignupRequestBusiness;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.SignupResponseBusiness;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationBusinessFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSignUpTask = null;
    private SignupResponseBusiness mSignupResponseBusiness;

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

    private Button mActivateButton;
    private Button mResendOTPButton;
    private EditText mOTPEditText;
    private TextView mTimerTextView;

    private String mDeviceID;
    private ProgressDialog mProgressDialog;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_business);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) v.findViewById(R.id.buttonResend);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);

        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        mDeviceID = telephonyManager.getDeviceId();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_logging_in));

        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SignupOrLoginActivity.mAccountType == Constants.BUSINESS_ACCOUNT_TYPE) {
                    if (Utilities.isConnectionAvailable(getActivity())) resendOTP();
                    else if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                } else {
                    ((SignupOrLoginActivity) getActivity()).switchToBusinessSignUpFragment();
                }
            }
        });

        mActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptSignUp();
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

    private void resendOTP() {
        if (SignupOrLoginActivity.mAccountType == Constants.BUSINESS_ACCOUNT_TYPE) {

            if (mRequestOTPTask != null) {
                return;
            }

            mProgressDialog.show();

            OTPRequestBusinessSignup mOtpRequestBusinessSignup = new OTPRequestBusinessSignup
                    (SignupOrLoginActivity.mMobileNumberBusiness,
                            Constants.MOBILE_ANDROID + mDeviceID, Constants.BUSINESS_ACCOUNT_TYPE);
            Gson gson = new Gson();
            String json = gson.toJson(mOtpRequestBusinessSignup);
            mRequestOTPTask = new
                    HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                    Constants.BASE_URL_POST_MM + Constants.URL_OTP_REQUEST_BUSINESS, json, getActivity()

            );
            mRequestOTPTask.mHttpResponseListener = this;
            mRequestOTPTask.execute((Void) null);
        }
    }

    private void attemptSignUp() {
        if (mSignUpTask != null) {
            return;
        }
        mProgressDialog.show();

        AddressClass mBusinessAddress = new AddressClass(SignupOrLoginActivity.mAddressLine1Business,
                SignupOrLoginActivity.mAddressLine2Business, SignupOrLoginActivity.mCityBusiness, SignupOrLoginActivity.mDistrictBusiness,
                SignupOrLoginActivity.mPostcodeBusiness, SignupOrLoginActivity.mCountryBusiness);

        AddressClass mBusinessHolderAddress = new AddressClass(SignupOrLoginActivity.mAddressLine1Business,
                SignupOrLoginActivity.mAddressLine2Business, SignupOrLoginActivity.mCityBusiness, SignupOrLoginActivity.mDistrictBusiness,
                SignupOrLoginActivity.mPostcodeBusiness, SignupOrLoginActivity.mCountryBusiness);

        SignupRequestBusiness mSignupModel = new SignupRequestBusiness(SignupOrLoginActivity.mMobileNumberBusiness,
                Constants.MOBILE_ANDROID + mDeviceID, SignupOrLoginActivity.mFirstNameBusiness, SignupOrLoginActivity.mAccountType,
                SignupOrLoginActivity.mLastNameBusiness, SignupOrLoginActivity.mBirthdayBusinessHolder,
                Utilities.md5(SignupOrLoginActivity.mPasswordBusiness), "M", mOTPEditText.getText().toString().trim(),
                SignupOrLoginActivity.mBusinessName, SignupOrLoginActivity.mTypeofBusiness,
                SignupOrLoginActivity.mEmailBusiness, SignupOrLoginActivity.mEmailBusiness,
                SignupOrLoginActivity.mMobileNumberBusiness, mBusinessAddress, mBusinessHolderAddress);
        Gson gson = new Gson();
        String json = gson.toJson(mSignupModel);
        mSignUpTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SIGN_UP_BUSINESS,
                Constants.BASE_URL_POST_MM + Constants.URL_SIGN_UP_BUSINESS, json, getActivity());
        mSignUpTask.mHttpResponseListener = this;
        mSignUpTask.execute((Void) null);

    }

    private void attemptLogin(String mUserNameLogin, String mPasswordLogin) {
        if (mLoginTask != null) {
            return;
        }

        mProgressDialog.show();
        LoginRequest mLoginModel = new LoginRequest(mUserNameLogin, Utilities.md5(mPasswordLogin),
                Constants.MOBILE_ANDROID + mDeviceID, null, null, null);
        Gson gson = new Gson();
        String json = gson.toJson(mLoginModel);
        mLoginTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_IN,
                Constants.BASE_URL_POST_MM + Constants.URL_LOGIN, json, getActivity());
        mLoginTask.mHttpResponseListener = this;
        mLoginTask.execute((Void) null);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mSignUpTask = null;
            mRequestOTPTask = null;
            mLoginTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SIGN_UP_BUSINESS)) {

            mSignupResponseBusiness = gson.fromJson(resultList.get(2), SignupResponseBusiness.class);
            String message = mSignupResponseBusiness.getMessage();

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
                pref.edit().putString(Constants.USERID, SignupOrLoginActivity.mMobileNumberBusiness).commit();
                pref.edit().putString(Constants.PASSWORD, SignupOrLoginActivity.mPasswordBusiness).commit();
                pref.edit().putString(Constants.FIRST_NAME, SignupOrLoginActivity.mFirstNameBusiness).commit();
                pref.edit().putString(Constants.LAST_NAME, SignupOrLoginActivity.mLastNameBusiness).commit();
                pref.edit().putString(Constants.BIRTHDAY, SignupOrLoginActivity.mBirthdayBusinessHolder).commit();
                pref.edit().putString(Constants.GENDER, "M").commit();
                pref.edit().putString(Constants.USERCOUNTRY, SignupOrLoginActivity.mCountryBusiness).commit();   // TODO
                pref.edit().putInt(Constants.ACCOUNT_TYPE, Constants.BUSINESS_ACCOUNT_TYPE).commit();
                pref.edit().putBoolean(Constants.LOGGEDIN, true).commit();

                // Request a login immediately after sign up
                attemptLogin(SignupOrLoginActivity.mMobileNumberBusiness, SignupOrLoginActivity.mPasswordBusiness);

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mSignUpTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_OTP_VERIFICATION)) {

            mOtpResponseBusinessSignup = gson.fromJson(resultList.get(2), OTPResponseBusinessSignup.class);
            String message = mOtpResponseBusinessSignup.getMessage();

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_ACCEPTED)) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.otp_sent, Toast.LENGTH_LONG).show();

                // Start timer again
                mTimerTextView.setVisibility(View.VISIBLE);
                mResendOTPButton.setEnabled(false);
                new CountDownTimer(60000, 1000) {

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

            mProgressDialog.dismiss();
            mRequestOTPTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_LOG_IN)) {

            mLoginResponseModel = gson.fromJson(resultList.get(2), LoginResponse.class);
            String message = mLoginResponseModel.getMessage();

            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                ((SignupOrLoginActivity) getActivity()).switchToHomeActivity();

                Firebase rootRef = new Firebase(Constants.PATH_TO_IPAY_USERS);
                Firebase newUserRef = rootRef.child(SignupOrLoginActivity.mMobileNumberBusiness);
                UserPersonal mUserPersonal = new UserPersonal(SignupOrLoginActivity.mMobileNumberBusiness, Constants.MOBILE_ANDROID + mDeviceID,
                        SignupOrLoginActivity.mFirstNameBusiness, SignupOrLoginActivity.mLastNameBusiness,
                        SignupOrLoginActivity.mBirthdayBusinessHolder, SignupOrLoginActivity.mPasswordBusiness, "M");
                newUserRef.setValue(mUserPersonal);

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mLoginTask = null;
        }
    }
}

