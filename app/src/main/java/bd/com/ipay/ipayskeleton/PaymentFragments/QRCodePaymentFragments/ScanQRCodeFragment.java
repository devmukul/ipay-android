package bd.com.ipay.ipayskeleton.PaymentFragments.QRCodePaymentFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ScanQRCodeFragment extends BaseFragment implements HttpResponseListener {

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestGetAsyncTask mGetUserInfoTask;

    private View mRootView;
    private ProgressDialog mProgressDialog;

    private String mobileNumber;
    private String name;
    private String imageUrl;
    private String address;
    private String country;
    private String district;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_scan_qr_code, container, false);
        }
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setCancelable(false);

        Utilities.performQRCodeScan(this, REQUEST_CODE_PERMISSION);
        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_scan_qr_code));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                getActivity().finish();
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                final Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (InputValidator.isValidNumber(result)) {
                            if (Utilities.isConnectionAvailable(getActivity())) {
                                mobileNumber = ContactEngine.formatMobileNumberBD(result);
                                GetUserInfoRequestBuilder getUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

                                if (mGetUserInfoTask != null) {
                                    return;
                                }

                                mProgressDialog.show();
                                mGetUserInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                                        getUserInfoRequestBuilder.getGeneratedUri(), getActivity());
                                mGetUserInfoTask.mHttpResponseListener = ScanQRCodeFragment.this;
                                mGetUserInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            } else {
                                Toaster.makeText(getActivity(), getResources().getString(
                                        R.string.no_internet_connection), Toast.LENGTH_SHORT);
                                mProgressDialog.cancel();
                                getActivity().finish();
                            }
                        } else if (getActivity() != null) {
                            DialogUtils.showDialogForInvalidQRCode(getActivity(), getString(R.string.scan_valid_ipay_qr_code));
                        }
                    }
                });
            }
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Utilities.initiateQRCodeScan(this);
                } else {
                    getActivity().finish();
                    Toaster.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG);
                }
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        if (result == null) {
            mGetUserInfoTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }
        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_USER_INFO:
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Gson gson = new GsonBuilder().create();
                    GetUserInfoResponse getUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                    imageUrl = getUserInfoResponse.getProfilePictures().get(0).getUrl();
                    name = getUserInfoResponse.getName();
                    if (getUserInfoResponse.getAddressList() != null) {
                        if (getUserInfoResponse.getAddressList().getOFFICE() != null) {
                            address = getUserInfoResponse.getAddressList().getOFFICE().get(0).getAddressLine1();
                            country = getUserInfoResponse.getAddressList().getOFFICE().get(0).getCountry();
                            district = getUserInfoResponse.getAddressList().getOFFICE().get(0).getDistrict();
                        }
                    }

                    // We will do a check here to know if the account is a personal account or business account.
                    // For Personal Account we have to launch the SendMoneyActivity
                    // For Business Account we have to launch the PaymentActivity with a account status verification check
                    if (getUserInfoResponse.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
                        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.SEND_MONEY)) {
                            DialogUtils.showServiceNotAllowedDialog(getContext());
                        } else {
                            switchActivity(SendMoneyActivity.class);
                        }
                    } else if (getUserInfoResponse.getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE) {
                        if (getUserInfoResponse.getAccountStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                            if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                                DialogUtils.showServiceNotAllowedDialog(getContext());
                            } else {
                                switchActivity(PaymentActivity.class);
                            }
                        } else {
                            DialogUtils.showDialogForInvalidQRCode(getActivity(), getString(R.string.business_account_not_verified));
                        }
                    }

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    DialogUtils.showDialogForInvalidQRCode(getActivity(), getString(R.string.scan_valid_ipay_qr_code));
                }
                break;
        }
    }

    private void switchActivity(Class tClass) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
        intent.putExtra(Constants.FROM_QR_SCAN, true);
        intent.putExtra(Constants.NAME, name);
        intent.putExtra(Constants.PHOTO_URI, imageUrl);
        intent.putExtra(Constants.COUNTRY, country);
        intent.putExtra(Constants.DISTRICT, district);
        intent.putExtra(Constants.ADDRESS, address);
        startActivity(intent);
        getActivity().finish();
    }
}
