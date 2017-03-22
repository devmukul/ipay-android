package bd.com.ipay.ipayskeleton.Service.FCM;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RefreshToken.FCMRefreshTokenRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;

public class TokenRefreshListenerService extends FirebaseInstanceIdService implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRefreshTokenAsyncTask = null;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token", "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if (Constants.DEBUG)
            Log.w("Token_Timer", "Refresh token called");

        if (mRefreshTokenAsyncTask != null) {
            mRefreshTokenAsyncTask = null;
        }

        String myDeviceID = DeviceInfoFactory.getDeviceId(this);
        FCMRefreshTokenRequest mFcmRefreshTokenRequest = new FCMRefreshTokenRequest(refreshedToken, myDeviceID, Constants.MOBILE_ANDROID);
        Gson gson = new Gson();
        String json = gson.toJson(mFcmRefreshTokenRequest);
        mRefreshTokenAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_FIREBASE_TOKEN,
                Constants.BASE_URL_NOTIFICATION + Constants.URL_REFRESH_FIREBASE_TOKEN, json, getApplicationContext());
        mRefreshTokenAsyncTask.mHttpResponseListener = this;

        mRefreshTokenAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mRefreshTokenAsyncTask = null;
            return;
        }
        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_REFRESH_FIREBASE_TOKEN)) {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                } else {
                    if (this != null)
                        Toast.makeText(this, result.toString(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            mRefreshTokenAsyncTask = null;
        }
    }
}

