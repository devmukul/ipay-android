package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.NotificationApi.RegisterFCMTokenToServerAsyncTask;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPRequestBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPResponseBusinessSignup;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.SignupRequestBusiness;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.SignupResponseBusiness;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.CustomCountDownTimer;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationBusinessFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSignUpTask = null;
    private SignupResponseBusiness mSignupResponseBusiness;

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseBusinessSignup mOtpResponseBusinessSignup;

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

    private AddToTrustedDeviceResponse mAddToTrustedDeviceResponse;
    private HttpRequestPostAsyncTask mAddTrustedDeviceTask = null;

    private Button mActivateButton;
    private TextView mResendOTPButton;
    private EditText mOTPEditText;
    private TextView mTimerTextView;

    private String mDeviceID;
    private String mDeviceName;

    private ProgressDialog mProgressDialog;

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_otp_verification_for_business);
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_business_otp_verifications) );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (TextView) v.findViewById(R.id.buttonResend);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);

        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());
        mDeviceName = DeviceInfoFactory.getDeviceName();

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
                if (SignupOrLoginActivity.mAccountType == Constants.BUSINESS_ACCOUNT_TYPE) {
                    if (Utilities.isConnectionAvailable(getActivity())) resendOTP();
                    else if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                } else {
                    ((SignupOrLoginActivity) getActivity()).switchToBusinessStepOneFragment();
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
        new CustomCountDownTimer(SignupOrLoginActivity.otpDuration, 500) {

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
                    Constants.BASE_URL_MM + Constants.URL_OTP_REQUEST_BUSINESS, json, getActivity()

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

            SignupRequestBusiness mSignupBusinessRequest = new SignupRequestBusiness.Builder()
                    .mobileNumber(SignupOrLoginActivity.mMobileNumberBusiness)
                    .deviceId(Constants.MOBILE_ANDROID + mDeviceID)
                    .name(SignupOrLoginActivity.mNameBusiness)
                    .accountType(SignupOrLoginActivity.mAccountType)
                    .dob(SignupOrLoginActivity.mBirthdayBusinessHolder)
                    .password(SignupOrLoginActivity.mPasswordBusiness)
                    .otp(otp)
                    .businessName(SignupOrLoginActivity.mBusinessName)
                    .businessType(SignupOrLoginActivity.mTypeofBusiness)
                    .personalEmail(SignupOrLoginActivity.mEmailBusiness)
                    .personalMobileNumber(SignupOrLoginActivity.mMobileNumberPersonal)
                    .businessAddress(SignupOrLoginActivity.mAddressBusiness)
                    .personalAddress(SignupOrLoginActivity.mAddressBusinessHolder)
                    .build();

            Gson gson = new Gson();
            String json = gson.toJson(mSignupBusinessRequest);
            mSignUpTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SIGN_UP_BUSINESS,
                    Constants.BASE_URL_MM + Constants.URL_SIGN_UP_BUSINESS, json, getActivity());
            mSignUpTask.mHttpResponseListener = this;
            mSignUpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void attemptLogin(String mUserNameLogin, String mPasswordLogin, String otp) {
        if (mLoginTask != null) {
            return;
        }

        mProgressDialog.show();
        LoginRequest mLoginModel = new LoginRequest(mUserNameLogin, mPasswordLogin,
                Constants.MOBILE_ANDROID + mDeviceID, null, otp, null, null);
        Gson gson = new Gson();
        String json = gson.toJson(mLoginModel);
        mLoginTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_IN,
                Constants.BASE_URL_MM + Constants.URL_LOGIN, json, getActivity());
        mLoginTask.mHttpResponseListener = this;
        mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            hideProgressDialog();

            mSignUpTask = null;
            mRequestOTPTask = null;
            mLoginTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_SIGN_UP_BUSINESS:
                hideProgressDialog();

                try {
                    mSignupResponseBusiness = gson.fromJson(result.getJsonString(), SignupResponseBusiness.class);
                    String message = mSignupResponseBusiness.getMessage();
                    String otp = mSignupResponseBusiness.getOtp();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        ProfileInfoCacheManager.setMobileNumber(SignupOrLoginActivity.mMobileNumberBusiness);
                        ProfileInfoCacheManager.setName(SignupOrLoginActivity.mNameBusiness);
                        ProfileInfoCacheManager.setBirthday(SignupOrLoginActivity.mBirthdayBusinessHolder);
                        ProfileInfoCacheManager.setGender("M");
                        ProfileInfoCacheManager.setAccountType(Constants.BUSINESS_ACCOUNT_TYPE);

                        if (getActivity() != null)
                            Toast.makeText(getActivity(), getString(R.string.signup_successful), Toast.LENGTH_LONG).show();

                        // Request a login immediately after sign up
                        attemptLogin(SignupOrLoginActivity.mMobileNumberBusiness, SignupOrLoginActivity.mPasswordBusiness, otp);

                        // TODO: For now, switch to login fragment after a successful sign up. Don't remove it either. Can be used later
//                ((SignupOrLoginActivity) getActivity()).switchToLoginFragment();


                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mSignUpTask = null;

                break;

            case Constants.COMMAND_OTP_VERIFICATION:
                hideProgressDialog();

                try {
                    mOtpResponseBusinessSignup = gson.fromJson(result.getJsonString(), OTPResponseBusinessSignup.class);
                    String message = mOtpResponseBusinessSignup.getMessage();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.otp_sent, Toast.LENGTH_LONG).show();

                        // Start timer again
                        mTimerTextView.setVisibility(View.VISIBLE);
                        mResendOTPButton.setEnabled(false);
                        new CustomCountDownTimer(SignupOrLoginActivity.otpDuration, 500) {

                            public void onTick(long millisUntilFinished) {
                                mTimerTextView.setText(new SimpleDateFormat("mm:ss").format(new Date(millisUntilFinished)));
                            }

                            public void onFinish() {
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
                break;

            case Constants.COMMAND_LOG_IN:
                try {
                    mLoginResponseModel = gson.fromJson(result.getJsonString(), LoginResponse.class);
                    String message = mLoginResponseModel.getMessage();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        ProfileInfoCacheManager.setLoggedInStatus(true);

                        String pushRegistrationID = ProfileInfoCacheManager.getPushNotificationToken(null);
                        if (pushRegistrationID != null)
                            new RegisterFCMTokenToServerAsyncTask(getContext());

                        // Saving the allowed services id for the user
                        if (mLoginResponseModel.getAccessControlList() != null) {
                            ACLManager.updateAllowedServiceArray(mLoginResponseModel.getAccessControlList());
                        }

                        attemptAddTrustedDevice();

                    } else {
                        hideProgressDialog();

                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    hideProgressDialog();

                    e.printStackTrace();
                }

                mLoginTask = null;
                break;
            case Constants.COMMAND_ADD_TRUSTED_DEVICE:
                hideProgressDialog();

                try {
                    mAddToTrustedDeviceResponse = gson.fromJson(result.getJsonString(), AddToTrustedDeviceResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String UUID = mAddToTrustedDeviceResponse.getUUID();
                        ProfileInfoCacheManager.setUUID(UUID);

                        // Launch HomeActivity from here on successful trusted device add
                        ((SignupOrLoginActivity) getActivity()).switchToHomeActivity();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE)
                        ((SignupOrLoginActivity) getActivity()).switchToDeviceTrustActivity();
                    else
                        Toast.makeText(getActivity(), mAddToTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
                }

                mAddTrustedDeviceTask = null;
                break;
            default:
                hideProgressDialog();

                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void hideProgressDialog() {
        if (isAdded()) mProgressDialog.dismiss();
    }

    private void attemptAddTrustedDevice() {
        if (mAddTrustedDeviceTask != null)
            return;

        AddToTrustedDeviceRequest mAddToTrustedDeviceRequest = new AddToTrustedDeviceRequest(mDeviceName,
                Constants.MOBILE_ANDROID + mDeviceID, null);
        Gson gson = new Gson();
        String json = gson.toJson(mAddToTrustedDeviceRequest);
        mAddTrustedDeviceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_ADD_TRUSTED_DEVICE, json, getActivity());
        mAddTrustedDeviceTask.mHttpResponseListener = this;
        mAddTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}