package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.NotificationApi.RegisterFCMTokenToServerAsyncTask;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.EnableDisableSMSBroadcastReceiver;
import bd.com.ipay.ipayskeleton.BroadcastReceivers.SMSReaderBroadcastReceiver;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LoginRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LoginResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPRequestTrustedDevice;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.OTPResponseTrustedDevice;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice.AddToTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.CustomCountDownTimer;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class OTPVerificationTrustFragment extends BaseFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLoginTask = null;
    private LoginResponse mLoginResponseModel;

    private HttpRequestPostAsyncTask mRequestOTPTask = null;
    private OTPResponseTrustedDevice mOTPResponseTrustedDevice;

    private AddToTrustedDeviceResponse mAddToTrustedDeviceResponse;
    private HttpRequestPostAsyncTask mAddTrustedDeviceTask = null;

    private HttpRequestGetAsyncTask mGetProfileCompletionStatusTask = null;
    private ProfileCompletionStatusResponse mProfileCompletionStatusResponse;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetProfileInfoResponse mGetProfileInfoResponse;

    private Button mActivateButton;
    private EditText mOTPEditText;
    private TextView mTimerTextView;
    private Button mResendOTPButton;

    private String mDeviceID;
    private String mDeviceName;

    private ProgressDialog mProgressDialog;

    private EnableDisableSMSBroadcastReceiver mEnableDisableSMSBroadcastReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_otp_verification_trusted_device, container, false);
        mActivateButton = (Button) v.findViewById(R.id.buttonVerifyOTP);
        mResendOTPButton = (Button) v.findViewById(R.id.buttonResend);
        mOTPEditText = (EditText) v.findViewById(R.id.otp_edittext);
        mTimerTextView = (TextView) v.findViewById(R.id.txt_timer);

        mDeviceID = DeviceInfoFactory.getDeviceId(getActivity());
        mDeviceName = DeviceInfoFactory.getDeviceName();

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_logging_in));
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
                    attemptLogin(SignupOrLoginActivity.mMobileNumber, SignupOrLoginActivity.mPassword);

                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mResendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity()))
                    resendOTP(SignupOrLoginActivity.mMobileNumber, SignupOrLoginActivity.mPassword);
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

            }
        });

        mResendOTPButton.setEnabled(false);
        mTimerTextView.setVisibility(View.VISIBLE);
        new CustomCountDownTimer(SignupOrLoginActivity.otpDuration, 500) {

            public void onTick(long millisUntilFinished) {
                mTimerTextView.setText(new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(millisUntilFinished)));
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
        getActivity().setTitle(R.string.title_otp_verification_for_add_trusted_device);
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_otp_for_login));
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

    private void resendOTP(String mUserNameLogin, String mPasswordLogin) {

        if (mRequestOTPTask != null) {
            return;
        }

        mProgressDialog.show();

        OTPRequestTrustedDevice mOTPRequestTrustedDevice = new OTPRequestTrustedDevice
                (mUserNameLogin, mPasswordLogin,
                        Constants.MOBILE_ANDROID + mDeviceID, SignupOrLoginActivity.mAccountType);
        Gson gson = new Gson();
        String json = gson.toJson(mOTPRequestTrustedDevice);
        mRequestOTPTask = new HttpRequestPostAsyncTask(Constants.COMMAND_OTP_VERIFICATION,
                Constants.BASE_URL_MM + Constants.URL_LOGIN, json, getActivity());
        mRequestOTPTask.mHttpResponseListener = this;
        mRequestOTPTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptLogin(String mUserNameLogin, String mPasswordLogin) {
        if (mLoginTask != null) {
            return;
        }
        String otp = mOTPEditText.getText().toString().trim();
        String errorMessage = InputValidator.isValidOTP(getActivity(), otp);
        if (errorMessage != null) {
            mOTPEditText.requestFocus();
            mOTPEditText.setError(errorMessage);
        } else {
            mProgressDialog.show();

            LoginRequest mLoginModel = new LoginRequest(mUserNameLogin, mPasswordLogin,
                    Constants.MOBILE_ANDROID + mDeviceID, null, otp, null, null, SignupOrLoginActivity.isRememberMe);
            Gson gson = new Gson();
            String json = gson.toJson(mLoginModel);
            mLoginTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_IN,
                    Constants.BASE_URL_MM + Constants.URL_LOGIN, json, getActivity());
            mLoginTask.mHttpResponseListener = this;
            mLoginTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            hideProgressDialog();

            mLoginTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_LOG_IN:
                try {
                    mLoginResponseModel = gson.fromJson(result.getJsonString(), LoginResponse.class);
                    String message = mLoginResponseModel.getMessage();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        ProfileInfoCacheManager.setLoggedInStatus(true);

                        ProfileInfoCacheManager.setAccountType(mLoginResponseModel.getAccountType());

                        if (mLoginResponseModel.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE)
                            ProfileInfoCacheManager.setMobileNumber(SignupOrLoginActivity.mMobileNumber);
                        else
                            ProfileInfoCacheManager.setMobileNumber(SignupOrLoginActivity.mMobileNumberBusiness);

                        String pushRegistrationID = ProfileInfoCacheManager.getPushNotificationToken(null);
                        if (pushRegistrationID != null) {
                            new RegisterFCMTokenToServerAsyncTask(getContext());
                        }

                        SharedPrefManager.setUserCountry(SignupOrLoginActivity.mCountryCode);

                        // Saving the allowed services id for the user
                        if (mLoginResponseModel.getAccessControlList() != null) {
                            ACLManager.updateAllowedServiceArray(mLoginResponseModel.getAccessControlList());
                        }

                        // Save Remember me in shared preference
                        if (SignupOrLoginActivity.isRememberMe) {
                            SharedPrefManager.setRememberMeActive(SignupOrLoginActivity.isRememberMe);
                        }

                        attemptTrustedDeviceAdd();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        Utilities.sendBlockedEventTracker(mTracker, "Trusted Device", ProfileInfoCacheManager.getAccountId());

                        getActivity().finish();
                    } else {
                        hideProgressDialog();

                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    hideProgressDialog();

                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
                }
                mLoginTask = null;
                break;
            case Constants.COMMAND_OTP_VERIFICATION:
                hideProgressDialog();

                try {
                    mOTPResponseTrustedDevice = gson.fromJson(result.getJsonString(), OTPResponseTrustedDevice.class);
                    SignupOrLoginActivity.otpDuration = mOTPResponseTrustedDevice.getOtpValidFor();
                    String message = mOTPResponseTrustedDevice.getMessage();

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.otp_sent, Toast.LENGTH_LONG).show();

                        // Start timer again
                        mTimerTextView.setVisibility(View.VISIBLE);
                        mResendOTPButton.setEnabled(false);
                        new CustomCountDownTimer(SignupOrLoginActivity.otpDuration, 500) {

                            public void onTick(long millisUntilFinished) {
                                mTimerTextView.setText(new SimpleDateFormat("mm:ss", Locale.getDefault()).format(new Date(millisUntilFinished)));
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
            case Constants.COMMAND_ADD_TRUSTED_DEVICE:

                try {
                    mAddToTrustedDeviceResponse = gson.fromJson(result.getJsonString(), AddToTrustedDeviceResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String UUID = mAddToTrustedDeviceResponse.getUUID();
                        ProfileInfoCacheManager.setUUID(UUID);

                        //Google Analytic event
                        Utilities.sendSuccessEventTracker(mTracker, "Login to Home", ProfileInfoCacheManager.getAccountId());
                        getProfileInfo();
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_ACCEPTABLE) {
                        hideProgressDialog();
                        ((SignupOrLoginActivity) getActivity()).switchToDeviceTrustActivity();
                        //Google Analytic event
                        Utilities.sendSuccessEventTracker(mTracker, "Login to Add Trusted Device", ProfileInfoCacheManager.getAccountId());
                    } else {
                        hideProgressDialog();
                        Toast.makeText(getActivity(), mAddToTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                        //Google Analytic event
                        Utilities.sendFailedEventTracker(mTracker, "Login", ProfileInfoCacheManager.getAccountId(), mAddToTrustedDeviceResponse.getMessage());
                    }
                } catch (Exception e) {
                    hideProgressDialog();
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.failed_add_trusted_device, Toast.LENGTH_LONG).show();
                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Login", ProfileInfoCacheManager.getAccountId(), getString(R.string.failed_add_trusted_device));
                }

                mProgressDialog.dismiss();
                mAddTrustedDeviceTask = null;
                break;

            case Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS:
                hideProgressDialog();
                try {
                    mProfileCompletionStatusResponse = gson.fromJson(result.getJsonString(), ProfileCompletionStatusResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        ProfileInfoCacheManager.switchedFromSignup(false);
                        ProfileInfoCacheManager.uploadProfilePicture(mProfileCompletionStatusResponse.isPhotoUpdated());
                        ProfileInfoCacheManager.uploadIdentificationDocument(mProfileCompletionStatusResponse.isPhotoIdUpdated());
                        ProfileInfoCacheManager.addBasicInfo(mProfileCompletionStatusResponse.isOnboardBasicInfoUpdated());

                        if (!ProfileInfoCacheManager.isProfilePictureUploaded() || !ProfileInfoCacheManager.isIdentificationDocumentUploaded()
                                || !ProfileInfoCacheManager.isBasicInfoAdded()) {
                            ((SignupOrLoginActivity) getActivity()).switchToProfileCompletionHelperActivity();
                        } else {
                            ((SignupOrLoginActivity) getActivity()).switchToHomeActivity();
                        }
                    } else {
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), mProfileCompletionStatusResponse.getMessage(), Toast.LENGTH_LONG);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.failed_fetching_profile_completion_status, Toast.LENGTH_LONG);
                }
                mProgressDialog.dismiss();
                mGetProfileCompletionStatusTask = null;
                break;

            case Constants.COMMAND_GET_PROFILE_INFO_REQUEST:

                try {
                    mGetProfileInfoResponse = gson.fromJson(result.getJsonString(), GetProfileInfoResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        ProfileInfoCacheManager.updateProfileInfoCache(mGetProfileInfoResponse);
                        getProfileCompletionStatus();
                    } else {
                        hideProgressDialog();
                        Toaster.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                    }
                } catch (Exception e) {
                    hideProgressDialog();
                    e.printStackTrace();
                    Toaster.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                }
                mGetProfileInfoTask = null;
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

    private void attemptTrustedDeviceAdd() {
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

    private void getProfileCompletionStatus() {
        if (mGetProfileCompletionStatusTask != null) {
            return;
        }
        mGetProfileCompletionStatusTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_COMPLETION_STATUS,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_COMPLETION_STATUS, getActivity(), this);
        mGetProfileCompletionStatusTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getProfileInfo() {
        if (mGetProfileInfoTask != null) {
            return;
        }
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PROFILE_INFO_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_PROFILE_INFO_REQUEST, getActivity());
        mGetProfileInfoTask.mHttpResponseListener = this;
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}