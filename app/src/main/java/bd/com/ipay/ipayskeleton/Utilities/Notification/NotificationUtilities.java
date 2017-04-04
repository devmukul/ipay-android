package bd.com.ipay.ipayskeleton.Utilities.Notification;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class NotificationUtilities {
    public static void sendBroadcast(Context context, String intentFilter) {
        Intent intent = new Intent(intentFilter);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
