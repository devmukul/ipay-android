package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.GetRefreshTokenResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public abstract class BaseActivity extends AppCompatActivity implements HttpResponseListener {

    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;

    private Context context;

    public static final long DISCONNECT_TIMEOUT = 300000; // 5 min = 5 * 60 * 1000 ms

    public BaseActivity() {
        this.context = setContext();
    }

    public abstract Context setContext();

    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            if (!((Activity) context).isFinishing()) {
                if (Utilities.isConnectionAvailable(context)) attemptLogout();
                else {
                    Intent intent = new Intent(context, SignupOrLoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }

    public void refreshToken() {
        if (HomeActivity.mRefreshTokenAsyncTask != null) {
            HomeActivity.mRefreshTokenAsyncTask.cancel(true);
            HomeActivity.mRefreshTokenAsyncTask = null;
        }

        GetRefreshTokenRequest mGetRefreshTokenRequest = new GetRefreshTokenRequest(HomeActivity.iPayRefreshToken);
        Gson gson = new Gson();
        String json = gson.toJson(mGetRefreshTokenRequest);
        HomeActivity.mRefreshTokenAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_TOKEN,
                Constants.BASE_URL + Constants.URL_GET_REFRESH_TOKEN, json, context);
        HomeActivity.mRefreshTokenAsyncTask.mHttpResponseListener = this;

        HomeActivity.mRefreshTokenAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        SharedPreferences pref;
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        String mUserID = pref.getString(Constants.USERID, "");

        LogoutRequest mLogoutModel = new LogoutRequest(mUserID);
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);
        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL + Constants.URL_LOG_OUT, json, context);
        mLogoutTask.mHttpResponseListener = this;

        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mLogoutTask = null;
            HomeActivity.mRefreshTokenAsyncTask = null;
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_LOG_OUT)) {

            try {
                if (resultList.size() > 2) {
                    mLogOutResponse = gson.fromJson(resultList.get(2), LogoutResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        finish();
                        Intent intent = new Intent(context, SignupOrLoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else
                    Toast.makeText(context, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
            }

            mLogoutTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_REFRESH_TOKEN)) {

            try {
                if (resultList.size() > 2) {
                    HomeActivity.mGetRefreshTokenResponse = gson.fromJson(resultList.get(2), GetRefreshTokenResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                    } else {
                        Toast.makeText(context, HomeActivity.mGetRefreshTokenResponse.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        Intent intent = new Intent(context, SignupOrLoginActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(context, R.string.please_login_again, Toast.LENGTH_LONG).show();
                    finish();
                    Intent intent = new Intent(context, SignupOrLoginActivity.class);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, R.string.please_login_again, Toast.LENGTH_LONG).show();
                finish();
                Intent intent = new Intent(context, SignupOrLoginActivity.class);
                startActivity(intent);
            }

            HomeActivity.mRefreshTokenAsyncTask = null;

        }
    }
}