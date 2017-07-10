package bd.com.ipay.ipayskeleton.PaymentFragments.QRCodePaymentFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.QRCodePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

/**
 * Created by sajid.shahriar on 7/10/17.
 */

public class ScanQRCodeFragment extends Fragment implements HttpResponseListener {

    private View mRootView;

    private ProgressDialog mProgressDialog;

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private HttpRequestGetAsyncTask mGetUserInfoTask;

    private String mobileNumber;


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
                        if (ContactEngine.isValidNumber(result)) {
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
                            showAlertDialog(getString(R.string.please_scan_a_valid_pin));
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
                    if (getUserInfoResponse.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
                        switchActivityForPayment(SendMoneyActivity.class);
                    } else if (getUserInfoResponse.getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE) {
                        if (getUserInfoResponse.getAccountStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                            switchActivityForPayment(PaymentActivity.class);
                        } else {
                            showAlertDialog(getString(R.string.business_account_not_verified));
                        }
                    }
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    showAlertDialog(getString(R.string.please_scan_a_valid_pin));
                }
                break;
        }
    }

    private void showAlertDialog(String message) {
        MaterialDialog materialDialog;
        MaterialDialog.Builder materialDialogBuilder = new MaterialDialog.Builder(getActivity());
        materialDialogBuilder.positiveText(R.string.ok);
        materialDialogBuilder.content(message);
        materialDialogBuilder.dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent(getActivity(), QRCodePaymentActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();

            }
        });
        materialDialog = materialDialogBuilder.build();
        materialDialog.show();
    }

    private void switchActivityForPayment(Class tClass) {
        Intent intent = new Intent(getActivity(), tClass);
        intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
        startActivity(intent);
        getActivity().finish();
    }
}
