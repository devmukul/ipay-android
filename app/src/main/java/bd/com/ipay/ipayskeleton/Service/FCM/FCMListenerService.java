package bd.com.ipay.ipayskeleton.Service.FCM;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMListenerService  extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();

        Log.d("Message", "From: " + from);

        // Check if message contains a data payload.
        if (data.size() > 0) {
            Log.d("Data", "Message data payload: " + data.toString());
        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d("Notification Payload", "Message Notification Body: " + message.getNotification().getBody());
        }
    }
}