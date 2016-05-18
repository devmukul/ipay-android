package bd.com.ipay.ipayskeleton.Service.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GcmListenerService extends com.google.android.gms.gcm.GcmListenerService {

    private String title;
    private String message;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        title = data.getString(Constants.PUSH_NOTIFICATION_TAG_TITLE);
        message = data.getString(Constants.PUSH_NOTIFICATION_TAG_MESSAGE);

        // TODO: Check later
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }

        createNotification(title, message);
    }

    //Create and show a simple notification containing the received GCM message.
    private void createNotification(String title, String message) {

        int notificationID = new Random().nextInt();

        Intent intent = new Intent(this, HomeActivity
                .class);
        // TODO: Put extras in the intent to redirect to corresponding pages from here.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationID, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.icon_ipay)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, notificationBuilder.build());

        // TODO: Necessary database operation if needed

    }
}