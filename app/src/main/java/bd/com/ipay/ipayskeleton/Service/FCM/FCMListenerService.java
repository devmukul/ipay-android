package bd.com.ipay.ipayskeleton.Service.FCM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Map;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.NotificationApi.CreateCustomNotificationAsyncTask;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RefreshToken.FCMRefreshTokenRequest;
import bd.com.ipay.ipayskeleton.Utilities.AppInstance.AppInstanceUtilities;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DeviceInfoFactory;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class FCMListenerService extends FirebaseMessagingService implements HttpResponseListener {
    private FCMNotificationResponse mFcmNotificationResponse;
    private RemoteMessage.Notification notification;

    private Map data;
    private String from;
    private int serviceId;

    private HttpRequestPostAsyncTask mRefreshTokenAsyncTask;

    private SharedPreferences pref;

    private String firebaseToken;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        firebaseToken = s;
        boolean isLoggedIn = ProfileInfoCacheManager.getLoggedInStatus(false);
        if (isLoggedIn) {
            sendFireBaseTokenToServer();
        } else {
            pref = this.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
            pref.edit().putString(SharedPrefConstants.PUSH_NOTIFICATION_TOKEN, s).apply();
        }

    }

    private void sendFireBaseTokenToServer() {
        String fireBaseToken = FirebaseInstanceId.getInstance().getToken();
        Logger.logW("Firebase Token", "Refresh token called");

        if (mRefreshTokenAsyncTask != null) {
            mRefreshTokenAsyncTask = null;
        }

        String myDeviceID = DeviceInfoFactory.getDeviceId(this);
        FCMRefreshTokenRequest mFcmRefreshTokenRequest = new FCMRefreshTokenRequest(fireBaseToken, myDeviceID, Constants.MOBILE_ANDROID);
        Gson gson = new Gson();
        String json = gson.toJson(mFcmRefreshTokenRequest);
        mRefreshTokenAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REFRESH_FIREBASE_TOKEN,
                Constants.BASE_URL_PUSH_NOTIFICATION + Constants.URL_REFRESH_FIREBASE_TOKEN, json, this, true);
        mRefreshTokenAsyncTask.mHttpResponseListener = this;

        mRefreshTokenAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        parseRemoteMessage(message);
    }

    private void parseRemoteMessage(RemoteMessage message) {

        from = message.getFrom();
        data = message.getData();

        Logger.logD("Message", "From: " + from);

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Logger.logD("Data", "Message data payload: " + data.toString());
            setNotificationResponseFromData(data);
        }

        serviceId = FCMNotificationParser.parseServiceID(mFcmNotificationResponse);

        // Check if message contains a notification payload.
        if (!(AppInstanceUtilities.isUserActive(this))
                || serviceId == Constants.SERVICE_ID_BATCH_NOTIFICATION
                || serviceId == Constants.SERVICE_ID_DEEP_LINK_NOTIFICATION) {
            if (data != null) {
                //Logger.logD("Notification Payload", "Message Notification Body: " + notification.getBody());
                try {
                    createNotification(this, data.values().toArray()[5].toString(),
                            data.values().toArray()[3].toString(), mFcmNotificationResponse.getIcon());
                } catch (Exception e) {

                }
            }
        } else {
            FCMNotificationParser.parseInAppNotification(this, mFcmNotificationResponse);
        }

    }

    private void setNotificationResponseFromData(Map data) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(data);
        mFcmNotificationResponse = gson.fromJson(jsonElement, FCMNotificationResponse.class);
    }

    private void createNotification(Context context, String title, String message, String imageUrl) {
        if (serviceId == Constants.SERVICE_ID_DEEP_LINK_NOTIFICATION) {
            new CreateCustomNotificationAsyncTask(context, title,
                    message, imageUrl, mFcmNotificationResponse.getDeepLink(), mFcmNotificationResponse.getTime()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new CreateCustomNotificationAsyncTask(context, title,
                    message, imageUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, this, null)) {
            mRefreshTokenAsyncTask = null;
            return;
        } else {
            mRefreshTokenAsyncTask = null;
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                pref.edit().putString(SharedPrefConstants.PUSH_NOTIFICATION_TOKEN, null).apply();
            } else {
                pref.edit().putString(SharedPrefConstants.PUSH_NOTIFICATION_TOKEN, firebaseToken).apply();
            }
        }
    }
}