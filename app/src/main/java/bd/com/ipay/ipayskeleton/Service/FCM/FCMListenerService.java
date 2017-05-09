package bd.com.ipay.ipayskeleton.Service.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.Map;
import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.AppInstance.AppInstanceUtilities;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class FCMListenerService extends FirebaseMessagingService {
    private FCMNotificationResponse mFcmNotificationResponse;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        String from = message.getFrom();
        Map data = message.getData();

        Logger.logD("Message", "From: " + from);

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Logger.logD("Data", "Message data payload: " + data.toString());
            parseNotificationResponseFromData(data);
        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Logger.logD("Notification Payload", "Message Notification Body: " + message.getNotification().getBody());

            if (!(AppInstanceUtilities.isUserActive(this)))
                createNotification(this, message.getNotification().getTitle(),
                        message.getNotification().getBody(), mFcmNotificationResponse.getIcon());
        }
    }

    private void parseNotificationResponseFromData(Map data) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(data);
        mFcmNotificationResponse = gson.fromJson(jsonElement, FCMNotificationResponse.class);

        if (AppInstanceUtilities.isUserActive(this))
            FCMNotificationParser.notificationParser(this, mFcmNotificationResponse);
    }

    private void createNotification(Context context, String title, String message, String imageUrl) {
        int notificationID = new Random().nextInt();
        Intent intent = new Intent(this, SignupOrLoginActivity.class);


        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID, notificationBuilder.build());
    }
}