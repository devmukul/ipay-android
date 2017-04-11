package bd.com.ipay.ipayskeleton.Service.FCM;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Map;

import bd.com.ipay.ipayskeleton.Api.NotificationApi.CreateCustomNotificationAsyncTask;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Notification.NotificationUtilities;

public class FCMListenerService extends FirebaseMessagingService {
    private FCMNotificationResponse mFcmNotificationResponse;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();

        if (Constants.DEBUG) Log.d("Message", "From: " + from);

        // Check if message contains a data payload.
        if (data.size() > 0) {
            if (Constants.DEBUG) Log.d("Data", "Message data payload: " + data.toString());
            parseNotificationResponseFromData(data);
        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            if (Constants.DEBUG)
                Log.d("Notification Payload", "Message Notification Body: " + message.getNotification().getBody());

            if (!(NotificationUtilities.isUserActive(this)))
                createNotification(this, message.getNotification().getTitle(),
                        message.getNotification().getBody(), mFcmNotificationResponse.getIcon());
        }
    }

    private void parseNotificationResponseFromData(Map data) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(data);
        mFcmNotificationResponse = gson.fromJson(jsonElement, FCMNotificationResponse.class);

        if (NotificationUtilities.isUserActive(this))
            FCMNotificationParser.notificationParser(this, mFcmNotificationResponse);
    }

    private void createNotification(Context context, String title, String message, String imageUrl) {
        new CreateCustomNotificationAsyncTask(context, title,
                message, imageUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}