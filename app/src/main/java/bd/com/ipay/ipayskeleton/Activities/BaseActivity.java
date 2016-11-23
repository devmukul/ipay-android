package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public abstract class BaseActivity extends AppCompatActivity implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;
    public static CountDownTimer tokenTimer;

    private final Context context;

    private static final long DISCONNECT_TIMEOUT = 300000; // 5 min = 5 * 60 * 1000 ms

    public BaseActivity() {
        this.context = setContext();

        // If the token is already initialized. Do not initialize again.
        if (tokenTimer != null) return;

        // Initialize token timer
        tokenTimer = new CountDownTimer(TokenManager.getiPayTokenTimeInMs(), 1000) {

            public void onTick(long millisUntilFinished) {
//                if (Constants.DEBUG)
//                    Log.w("Token_Timer_Tick", millisUntilFinished + "");
            }

            public void onFinish() {
//                if (Constants.DEBUG)
//                    Log.w("Token_Timer", "Token timer finished");
                refreshToken();
            }
        }.start();

//        if (Constants.DEBUG)
//            Log.w("Token_Timer", "Token timer initialized " + TokenManager.getiPayTokenTimeInMs() + "");
        TokenManager.setTokenTimer(tokenTimer);
    }

    protected abstract Context setContext();

    private final Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private final Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            if (!((Activity) context).isFinishing()) {
                if (Utilities.isConnectionAvailable(context)) attemptLogout();
                else {

                    boolean loggedIn = ProfileInfoCacheManager.getLoggedInStatus(true);

                    if (loggedIn) {
                        ProfileInfoCacheManager.setLoggedInStatus(false);

                        Intent intent = new Intent(context, SignupOrLoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    };

    private void resetDisconnectTimer() {
//        if (Constants.DEBUG)
//            Log.w("User_Interaction", "User interaction timer started");
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    private void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resets the user interaction timer
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    void refreshToken() {

//        if (Constants.DEBUG)
//            Log.w("Token_Timer", "Refresh token called");

        if (HomeActivity.mRefreshTokenAsyncTask != null) {
            HomeActivity.mRefreshTokenAsyncTask.cancel(true);
            HomeActivity.mRefreshTokenAsyncTask = null;
        }

        GetRefreshTokenRequest mGetRefreshTokenRequest = new GetRefreshTokenRequest(TokenManager.getRefreshToken());
        Gson gson = new Gson();
        String json = gson.toJson(mGetRefreshTokenRequest);
        HomeActivity.mRefreshTokenAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_TOKEN,
                Constants.BASE_URL_MM + Constants.URL_GET_REFRESH_TOKEN, json, context);
        HomeActivity.mRefreshTokenAsyncTask.mHttpResponseListener = this;

        HomeActivity.mRefreshTokenAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        SharedPreferences pref;
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        String mUserID = ProfileInfoCacheManager.getMobileNumber();

        LogoutRequest mLogoutModel = new LogoutRequest(mUserID);
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        // Set the preference
        ProfileInfoCacheManager.setLoggedInStatus(false);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, context);
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mLogoutTask = null;
            return;
        }

        Gson gson = new Gson();


        if (result.getApiCommand().equals(Constants.COMMAND_LOG_OUT)) {

            try {

                mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    finish();
                    Intent intent = new Intent(context, SignupOrLoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mLogoutTask = null;

        }
    }
}