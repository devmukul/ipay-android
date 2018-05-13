package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.CancelOrderRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.CancelOrderResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.GetOrderDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.GetOrderDetailsRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.GetPayByDeepLinkResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PayOrderRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRequestByDeepLink;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class MakePaymentByDeepLinkFragment extends Fragment implements LocationListener, HttpResponseListener {

    private HttpRequestGetAsyncTask mGetBusinessRuleTask;

    private HttpRequestDeleteAsyncTask mCancelOrderTask;
    private CancelOrderResponse mCancelOrderResponse;

    private HttpRequestGetAsyncTask mGetOrderDetailsTask;
    private GetOrderDetails mGetOrderDetails;

    private HttpRequestPostAsyncTask mPaymentTask = null;
    private PaymentRequestByDeepLink mPaymentRequestByDeepLink;

    private ProgressDialog mProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog
            mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private TextView mBusinessNameTextView;
    private TextView mAmountTextView;
    private TextView mDescriptionTextView;
    private ProfileImageView mBusinessLogoImageView;
    private Button mConfirmButton;
    private Button mCancelButton;

    private String thirdPartyAppUrl;
    private String orderID;
    private String otp;

    private LocationManager locationManager;
    private Location userLocation;

    private CustomProgressDialog mCustomProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay_by_deep_link, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        attemptGetBusinessRules(Constants.SERVICE_ID_MAKE_PAYMENT);
        SharedPrefManager.setFirstLaunch(false);
        mCustomProgressDialog = new CustomProgressDialog(getContext());
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        mBusinessNameTextView = (TextView) view.findViewById(R.id.business_name_text_view);
        mAmountTextView = (TextView) view.findViewById(R.id.amount_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        mBusinessLogoImageView = (ProfileImageView) view.findViewById(R.id.business_profile_image_view);
        mConfirmButton = (Button) view.findViewById(R.id.make_payment_button);
        mCancelButton = (Button) view.findViewById(R.id.cancel_button);
        getOrderID();
        setButtonActions();
    }

    private void getOrderID() {
        orderID = getActivity().getIntent().getStringExtra(Constants.ORDER_ID);
        getOrderDetails(orderID);
    }

    private void fillNecessaryfieldsWithData(GetOrderDetails getOrderDetails) {
        mBusinessNameTextView.setText(getOrderDetails.getMerchantName());
        mBusinessLogoImageView.setBusinessProfilePicture(
                Constants.BASE_URL_FTP_SERVER + getOrderDetails.getMerchantLogoUrl(), false);
        mAmountTextView.setText(Double.toString(getOrderDetails.getAmount()));
        mDescriptionTextView.setText(getOrderDetails.getDescription());
    }

    private void attemptGetBusinessRules(int serviceID) {
        if (mGetBusinessRuleTask != null)
            return;

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this, true);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setButtonActions() {
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    Utilities.hideKeyboard(getContext(), getView());
                    if (PaymentActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() &&
                            !ProfileInfoCacheManager.isAccountVerified()) {
                        DialogUtils.showDialogVerificationRequired(getContext());
                    } else {
                        attemptPaymentWithPinCheck();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.do_you_want_to_cancel_payment))
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                attemptCancelPayment();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void attemptCancelPayment() {
        if (mCancelOrderTask != null) {
            return;
        } else {
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.show();
            mCancelOrderTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_CANCEL_ORDER,
                    new CancelOrderRequestBuilder(orderID).getGeneratedUri(), getActivity(), false);
            mCancelOrderTask.mHttpResponseListener = this;
            mCancelOrderTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void attemptMakePayment(@Nullable Location location) {
        attemptPaymentWithPinCheck();
    }

    private void attemptPaymentWithPinCheck() {
        if (PaymentActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptPayment(pin);
                }
            });
        } else {
            attemptPayment(null);
        }

    }

    private void attemptPayment(String pin) {
        if (mPaymentTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.progress_dialog_text_payment));
        mCustomProgressDialog.showDialog();
        mPaymentRequestByDeepLink = new PaymentRequestByDeepLink(pin);

        String mUri = new PayOrderRequestBuilder(orderID).getGeneratedUri();
        mPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_PAYMENT_BY_DEEP_LINK,
                mUri, new Gson().toJson(mPaymentRequestByDeepLink), getActivity(), false);
        mPaymentTask.mHttpResponseListener = this;
        mPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchOtpVerification() {
        String jsonString = new Gson().toJson(mPaymentRequestByDeepLink);
        String mUri = new GetOrderDetailsRequestBuilder(orderID).getGeneratedUri();
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(),
                jsonString, Constants.COMMAND_PAYMENT,
                mUri, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
    }

    @SuppressLint("MissingPermission")
    private void getLocationAndAttemptMakePayment() {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.show();
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this, Looper.getMainLooper());
        } else {
            Utilities.showGPSHighAccuracyDialog(this);
        }
    }

    private void verifyOrderDetails(GetOrderDetails getOrderDetails) {
        if (getOrderDetails != null) {
            fillNecessaryfieldsWithData(getOrderDetails);
        }
    }

    public void showDialogAndLaunchThirdPartyApp(final String message) {
        appendSuccesOrFailureMessageAndLaunchThirdPartyApp(message);
    }

    private void launchParentThirdPartyApp() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(thirdPartyAppUrl));
            startActivity(intent);
            getActivity().finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOrderDetails(String orderID) {
        if (mGetOrderDetailsTask != null) {
            return;
        } else {
            mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.show();
            String mUri = new GetOrderDetailsRequestBuilder(orderID).getGeneratedUri();
            mGetOrderDetailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ORDER_DETAILS,
                    mUri, getActivity(), this, false);
            mGetOrderDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    private void appendSuccesOrFailureMessageAndLaunchThirdPartyApp(String message) {
        thirdPartyAppUrl = "ipay" + thirdPartyAppUrl + "://callbackhost/" + message;
        launchParentThirdPartyApp();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();

        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mCustomProgressDialog.dismissDialog();
            mGetOrderDetailsTask = null;
            mPaymentTask = null;
            getActivity().finish();
            return;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
            mProgressDialog.dismiss();
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {
                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MAX_AMOUNT_PER_PAYMENT)) {
                                PaymentActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_MIN_AMOUNT_PER_PAYMENT)) {
                                PaymentActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_VERIFICATION_REQUIRED)) {
                                PaymentActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_PIN_REQUIRED)) {
                                PaymentActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_MAKE_PAYMENT_LOCATION_REQUIRED)) {
                                PaymentActivity.mMandatoryBusinessRules.setLOCATION_REQUIRED(rule.getRuleValue());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                }
            } else {
                if (getActivity() != null)
                    DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            }

            mGetBusinessRuleTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_ORDER_DETAILS)) {
            mGetOrderDetails = new Gson().fromJson(result.getJsonString(), GetOrderDetails.class);
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    verifyOrderDetails(mGetOrderDetails);
                    thirdPartyAppUrl = mGetOrderDetails.getMerchantAppUriSchemeAndroid();
                } else {
                    DialogUtils.showNecessaryDialogForDeeplinkAction(getActivity(), mGetOrderDetails.getMessage());
                }
            } catch (Exception e) {
                DialogUtils.showNecessaryDialogForDeeplinkAction(getActivity(), mGetOrderDetails.getMessage());
            }
            mGetOrderDetailsTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_PAYMENT_BY_DEEP_LINK)) {
            final GetPayByDeepLinkResponse getPayByDeepLinkResponse = new Gson().
                    fromJson(result.getJsonString(), GetPayByDeepLinkResponse.class);
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mCustomProgressDialog.showSuccessAnimationAndMessage(getPayByDeepLinkResponse.getMessage());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            appendSuccesOrFailureMessageAndLaunchThirdPartyApp("success");
                        }
                    }, 2000);

                } else {
                    mCustomProgressDialog.showFailureAnimationAndMessage(getPayByDeepLinkResponse.getMessage());
                    if (!getPayByDeepLinkResponse.getMessage().toLowerCase().contains(Constants.invalid_credential)) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                appendSuccesOrFailureMessageAndLaunchThirdPartyApp("failed");
                            }
                        }, 2000);
                    }

                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), getString(R.string.payment_failed), Toast.LENGTH_LONG).show();
            }
            mPaymentTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_CANCEL_ORDER)) {
            mCancelOrderResponse = new Gson().fromJson(result.getJsonString(),
                    CancelOrderResponse.class);
            try {
                Toast.makeText(getContext(), mCancelOrderResponse.getMessage(), Toast.LENGTH_LONG).show();
                appendSuccesOrFailureMessageAndLaunchThirdPartyApp("cancelled");
            } catch (Exception e) {
                Toast.makeText(getActivity(), mCancelOrderResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
            mCancelOrderTask = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        attemptPaymentWithPinCheck();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
