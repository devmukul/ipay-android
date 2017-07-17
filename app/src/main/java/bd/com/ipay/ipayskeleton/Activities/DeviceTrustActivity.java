package bd.com.ipay.ipayskeleton.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.DeviceTrustFragments.AddTrustedDeviceFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.DeviceTrustFragments.RemoveTrustedDeviceFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;

public class DeviceTrustActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    private ProgressDialog mProgressDialog;
    private boolean switchedToRemoveTrustedDeviceFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_trust);

        mProgressDialog = new ProgressDialog(DeviceTrustActivity.this);
        switchToAddTrustedDeviceFragment();
    }

    @Override
    public void onBackPressed() {
        if (switchedToRemoveTrustedDeviceFragment)
            switchToAddTrustedDeviceFragment();
        else {
            attemptLogout();
        }
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_signing_out));
        mProgressDialog.show();
        LogoutRequest mLogoutModel = new LogoutRequest(ProfileInfoCacheManager.getMobileNumber());
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, DeviceTrustActivity.this);
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void switchToHomeActivity() {
        Intent intent = new Intent(DeviceTrustActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    public void switchToAddTrustedDeviceFragment() {
        switchedToRemoveTrustedDeviceFragment = false;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddTrustedDeviceFragment()).commit();
    }

    public void switchToRemoveTrustedDeviceFragment() {
        switchedToRemoveTrustedDeviceFragment = true;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RemoveTrustedDeviceFragment()).commit();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            Toast.makeText(DeviceTrustActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_LOG_OUT:
                try {
                    mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK)
                        ((MyApplication) this.getApplication()).launchLoginPage(null); // No message to display
                    else
                        Toast.makeText(DeviceTrustActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(DeviceTrustActivity.this, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mLogoutTask = null;
                break;
        }
    }

    @Override
    public Context setContext() {
        return DeviceTrustActivity.this;
    }
}



