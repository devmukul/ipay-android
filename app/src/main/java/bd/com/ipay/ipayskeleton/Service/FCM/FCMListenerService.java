package bd.com.ipay.ipayskeleton.Service.FCM;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Map;

import bd.com.ipay.ipayskeleton.Api.NotificationApi.CreateCustomNotificationAsyncTask;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.Utilities.AppInstance.AppInstanceUtilities;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class FCMListenerService extends FirebaseMessagingService {
    private FCMNotificationResponse mFcmNotificationResponse;
    private RemoteMessage.Notification notification;

    private Map data;
    private String from;
    private int serviceId;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        parseRemoteMessage(message);
    }

    private void parseRemoteMessage(RemoteMessage message) {
        from = message.getFrom();
        data = message.getData();
        notification = message.getNotification();

        Logger.logD("Message", "From: " + from);

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Logger.logD("Data", "Message data payload: " + data.toString());
            setNotificationResponseFromData(data);
        }

        serviceId = FCMNotificationParser.parseServiceID(mFcmNotificationResponse);

        // Check if message contains a notification payload.
        if (!(AppInstanceUtilities.isUserActive(this)) || serviceId == Constants.SERVICE_ID_BATCH_NOTIFICATION) {
            if (notification != null) {
                Logger.logD("Notification Payload", "Message Notification Body: " + notification.getBody());

                createNotification(this, notification.getTitle(),
                        notification.getBody(), mFcmNotificationResponse.getIcon());
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
        new CreateCustomNotificationAsyncTask(context, title,
                message, imageUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}