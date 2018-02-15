package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private TopupResponse mTopupResponse;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressDialog mProgressDialog;

    private double mAmount;
    private String mMobileNumber;
    private int mMobileNumberType;
    private String mCountryCode;
    private int mOperatorCode;
    private String mError_message;

    private TextView mReceiverNameView;
    private TextView mMobileNumberView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private TextView mPackageView;
    private TextView mOperatorView;
    private ProfileImageView mProfileImageView;
    private ImageView mOperatorImageView;
    private Button mTopupButton;

    private View mServiceCharge;
    private List<String> mArrayPackages;
    private List<String> mArrayOperators;

    private TopupRequest mTopupRequestModel;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private View v;
    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_mobile_topup_review));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mobile_topup_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mMobileNumberType = getActivity().getIntent().getIntExtra(Constants.MOBILE_NUMBER_TYPE, 1);
        mOperatorCode = getActivity().getIntent().getIntExtra(Constants.OPERATOR_CODE, 0);
        mCountryCode = getActivity().getIntent().getStringExtra(Constants.COUNTRY_CODE);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.image_view_profile);
        mReceiverNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mPackageView = (TextView) v.findViewById(R.id.textview_package);
        mOperatorView = (TextView) v.findViewById(R.id.textview_operator);
        mOperatorImageView = (ImageView) v.findViewById(R.id.imageView_operator);
        mTopupButton = (Button) v.findViewById(R.id.button_topup);
        mServiceCharge = v.findViewById(R.id.service_charge_with_net_amount);
        mProgressDialog = new ProgressDialog(getActivity());
        mMobileNumber = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);

        mMobileNumberView.setText(mMobileNumber);
        mAmountView.setText(Utilities.formatTaka(mAmount));
        getProfileInfo(mMobileNumber);

        mArrayPackages = Arrays.asList(getResources().getStringArray(R.array.package_type));
        mArrayOperators = Arrays.asList(getResources().getStringArray(R.array.mobile_operators));
        mPackageView.setText(mArrayPackages.get(mMobileNumberType - 1));
        mOperatorView.setText(mArrayOperators.get(mOperatorCode - 1));
        setOperatorIcon();

        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    mError_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmount),
                            TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (mError_message == null) {
                        attemptTopUpWithPinCheck();
                    } else {
                        showErrorDialog();
                    }
                } else {
                    attemptTopUpWithPinCheck();
                }
            }
        });

        attemptGetServiceCharge();

        return v;
    }

    private void attemptTopUpWithPinCheck() {
        if (TopUpActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptTopUp(pin);
                }
            });
        } else {
            attemptTopUp(null);
        }

    }

    private void attemptTopUp(String pin) {
        if (mTopupTask != null)
            return;
        mTopupRequestModel = new TopupRequest(Long.parseLong(mMobileNumber.replaceAll("[^0-9]", "")),
                mMobileNumber, mMobileNumberType, mOperatorCode, mAmount,
                mCountryCode, mMobileNumberType, Constants.DEFAULT_USER_CLASS, pin);

        mProgressDialog.setMessage(getString(R.string.dialog_requesting_top_up));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        Gson gson = new Gson();
        String json = gson.toJson(mTopupRequestModel);
        mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
        mTopupTask.mHttpResponseListener = this;
        mTopupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(mError_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_TOP_UP;
    }

    @Override
    public BigDecimal getAmount() {
        return new BigDecimal(mAmount);
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        if (serviceCharge.compareTo(BigDecimal.ZERO) == 0) {
            mServiceCharge.setVisibility(View.GONE);

        } else {
            mServiceCharge.setVisibility(View.VISIBLE);
            mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
            mTotalView.setText(Utilities.formatTaka(getAmount().subtract(serviceCharge)));

        }

    }

    private void setOperatorIcon() {
        //Setting the correct image based on Operator
        int[] images = {
                R.drawable.gp,
                R.drawable.gp,
                R.drawable.robi,
                R.drawable.airtel,
                R.drawable.banglalink,
                R.drawable.teletalk,
        };

        mOperatorImageView.setImageResource(images[mOperatorCode - 1]);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mTopupRequestModel);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }
        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getActivity(), this);

        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (isAdded())
            mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;
            mTopupTask = null;
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_TOPUP_REQUEST:

                try {
                    mTopupResponse = gson.fromJson(result.getJsonString(), TopupResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG);
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();

                            //Google Analytic event
                            Utilities.sendSuccessEventTracker(mTracker, "TopUp Processing", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                        }
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG);
                            getActivity().setResult(Activity.RESULT_OK);
                            getActivity().finish();

                            //Google Analytic event
                            Utilities.sendSuccessEventTracker(mTracker, "TopUp", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());
                        }
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BLOCKED) {
                        if (getActivity() != null)
                            ((MyApplication) getActivity().getApplication()).launchLoginPage(mTopupResponse.getMessage());
                        Utilities.sendBlockedEventTracker(mTracker, "Topup", ProfileInfoCacheManager.getAccountId(), Double.valueOf(mAmount).longValue());

                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_BAD_REQUEST) {
                        final String errorMessage;
                        if (!TextUtils.isEmpty(mTopupResponse.getMessage())) {
                            errorMessage = mTopupResponse.getMessage();
                        } else {
                            errorMessage = getString(R.string.recharge_failed);
                        }
                        Toaster.makeText(getActivity(), mTopupResponse.getMessage(), Toast.LENGTH_LONG);
                        //Google Analytic event
                        Utilities.sendFailedEventTracker(mTracker, "TopUp", ProfileInfoCacheManager.getAccountId(), errorMessage, Double.valueOf(mAmount).longValue());
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_ACCEPTED || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED) {
                        Toast.makeText(getActivity(), mTopupResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        SecuritySettingsActivity.otpDuration = mTopupResponse.getOtpValidFor();
                        launchOTPVerification();
                    } else {
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), mTopupResponse.getMessage(), Toast.LENGTH_LONG);

                        //Google Analytic event
                        Utilities.sendFailedEventTracker(mTracker, "TopUp", ProfileInfoCacheManager.getAccountId(), getString(R.string.recharge_failed), Double.valueOf(mAmount).longValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG);
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                }

                mTopupTask = null;

                break;
            case Constants.COMMAND_GET_USER_INFO:
                try {
                    mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        String name = mGetUserInfoResponse.getName();
                        String profilePicture = null;

                        if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                            profilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                        }
                        mReceiverNameView.setText(name);
                        mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + profilePicture, false);

                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                        String name = ContactEngine.getContactNameFromNumber(getActivity(), mMobileNumber);

                        if (name != null)
                            mReceiverNameView.setText(name);

                        String photoUri = ContactEngine.getPhotoUri(getActivity(), mMobileNumber);

                        if (photoUri != null)
                            mProfileImageView.setProfilePicture(photoUri, false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toaster.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                }

                mGetProfileInfoTask = null;

                break;
        }
    }
}
