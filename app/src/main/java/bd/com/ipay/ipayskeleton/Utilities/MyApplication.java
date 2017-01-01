package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Application;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class MyApplication extends Application implements HttpResponseListener {

    // Variables for user inactivity
    private Timer mUserInactiveTimer;
    private TimerTask mUserInactiveTimerTask;

    public boolean isAppInBackground = false;

    // Variables for token timer
    private Timer mTokenTimer;
    private TimerTask mTokenTimerTask;
    private HttpRequestPostAsyncTask mLogoutTask = null;
    private HttpRequestPostAsyncTask mRefreshTokenAsyncTask = null;
    private LogoutResponse mLogOutResponse;

    private static MyApplication myApplicationInstance;

    // 5 Minutes inactive time
    private final long AUTO_LOGOUT_TIMEOUT = 5 * 60 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        myApplicationInstance = this;

        ProfileInfoCacheManager.initialize(getApplicationContext());
        PushNotificationStatusHolder.initialize(getApplicationContext());

    }

    public static MyApplication getMyApplicationInstance() {
        return myApplicationInstance;
    }

    public void startUserInactivityDetectorTimer() {

        this.mUserInactiveTimer = new Timer();
        this.mUserInactiveTimerTask = new TimerTask() {
            public void run() {
                forceLogoutForInactivity();
            }
        };

        this.mUserInactiveTimer.schedule(mUserInactiveTimerTask,
                AUTO_LOGOUT_TIMEOUT);
    }

    public void stopUserInactivityDetectorTimer() {
        if (this.mUserInactiveTimerTask != null) {
            this.mUserInactiveTimerTask.cancel();
        }

        if (this.mUserInactiveTimer != null) {
            this.mUserInactiveTimer.cancel();
        }
    }

    public void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        String mUserID = ProfileInfoCacheManager.getMobileNumber();

        LogoutRequest mLogoutModel = new LogoutRequest(mUserID);
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, getApplicationContext());
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void startTokenTimer() {
        stopTokenTimer();

        this.mTokenTimer = new Timer();
        this.mTokenTimerTask = new TimerTask() {
            public void run() {
                refreshToken();
            }
        };

        this.mTokenTimer.schedule(mTokenTimerTask,
                TokenManager.getiPayTokenTimeInMs());
    }

    private void stopTokenTimer() {
        if (this.mTokenTimerTask != null) {
            this.mTokenTimerTask.cancel();
        }

        if (this.mTokenTimer != null) {
            this.mTokenTimer.cancel();
        }
    }

    private void refreshToken() {

        if (Constants.DEBUG)
            Log.w("Token_Timer", "Refresh token called");

        if (mRefreshTokenAsyncTask != null) {
            mRefreshTokenAsyncTask.cancel(true);
            mRefreshTokenAsyncTask = null;
        }

        GetRefreshTokenRequest mGetRefreshTokenRequest = new GetRefreshTokenRequest(TokenManager.getRefreshToken());
        Gson gson = new Gson();
        String json = gson.toJson(mGetRefreshTokenRequest);
        mRefreshTokenAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_TOKEN,
                Constants.BASE_URL_MM + Constants.URL_GET_REFRESH_TOKEN, json, getApplicationContext());
        mRefreshTokenAsyncTask.mHttpResponseListener = this;

        mRefreshTokenAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void forceLogoutForInactivity() {
        if (Utilities.isConnectionAvailable(getApplicationContext()))
            attemptLogout();
        
        else {
            launchLoginPage(getString(R.string.please_log_in_again));
        }
    }

    // Launch login page for token timeout/un-authorized/logout called for user inactivity
    public void launchLoginPage(String message) {
        boolean loggedIn = ProfileInfoCacheManager.getLoggedInStatus(true);

        // If the user is not logged in already, no need to launch the Login page.
        // Return from here
        if (!loggedIn) return;

        if (!isAppInBackground) {
            ProfileInfoCacheManager.setLoggedInStatus(false);

            Intent intent = new Intent(getApplicationContext(), SignupOrLoginActivity.class);
            if (message != null)
                intent.putExtra(Constants.MESSAGE, message);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        stopUserInactivityDetectorTimer();
        stopTokenTimer();
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mLogoutTask = null;
            mRefreshTokenAsyncTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_LOG_OUT)) {

            try {

                mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    launchLoginPage(getApplicationContext().getString(R.string.please_log_in_again));

                } else {
                    Toast.makeText(getApplicationContext(), mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mLogoutTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_REFRESH_TOKEN)) {

            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    // Do nothing
                } else {
                    launchLoginPage(getString(R.string.please_log_in_again));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mRefreshTokenAsyncTask = null;
        }
    }
}
