package bd.com.ipay.ipayskeleton.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Configuration.ApiVersionResponse;
import bd.com.ipay.ipayskeleton.Utilities.AppInstance.AppInstanceUtilities;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LauncherActivity extends AppCompatActivity implements HttpResponseListener {

    private boolean firstLaunch = false;

    private HttpRequestGetAsyncTask mCheckIfUpdateNeededAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogUtils.showAppUpdateDialog = null;

        if (!AppInstanceUtilities.isAppAlreadyLaunched(this)) {
            firstLaunch = SharedPrefManager.getFirstLaunch();
            startApplication();
        } else {
            finish();
        }
    }

    private void startApplication() {
        Intent intent;

        if (firstLaunch) {
            intent = new Intent(LauncherActivity.this, TourActivity.class);
            startActivity(intent);
            finish();
        } else {
            boolean loggedIn = ProfileInfoCacheManager.getLoggedInStatus(true);

            if (SharedPrefManager.isRememberMeActive() && loggedIn) {
                if (ProfileInfoCacheManager.isAccountSwitched()) {
                    TokenManager.setOnAccountId(ProfileInfoCacheManager.getOnAccountId());
                }
                checkIfUpdateNeeded();
            } else {
                ProfileInfoCacheManager.updateProfileInfoCache(Utilities.getMainUserInfoFromJsonString(ProfileInfoCacheManager.getMainUserProfileInfo()));
                intent = new Intent(LauncherActivity.this, SignupOrLoginActivity.class);
                intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
                startActivity(intent);
                finish();
            }

        }
    }

    private void checkIfUpdateNeeded() {
        if (mCheckIfUpdateNeededAsyncTask != null) {
            return;
        } else {
            mCheckIfUpdateNeededAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_CHECK_VERSION,
                    Constants.BASE_URL_MM + Constants.URL_GET_MIN_API_VERSION_REQUIRED, this, this);
            mCheckIfUpdateNeededAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            if (this != null) {
                Toast.makeText(this, "Can't start application", Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    int minApiVersion = new Gson().fromJson(result.getJsonString(), ApiVersionResponse.class).getAndroid();
                    if (minApiVersion > BuildConfig.VERSION_CODE) {
                        DialogUtils.showAppUpdateRequiredDialog(this);
                    } else {
                        Intent intent;
                        intent = new Intent(LauncherActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    finish();
                    Toast.makeText(this, "Can't start application", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                finish();
                Toast.makeText(this, "Can't start application", Toast.LENGTH_LONG).show();
            }

        }
    }
}
