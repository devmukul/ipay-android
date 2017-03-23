package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Service.FCM.FCMPushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class FireBaseNotificationParser {

    private static SharedPreferences pref;

    public static void notificationParser(Context context, FireBaseNotification fireBaseNotification) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        int serviceID = fireBaseNotification.getTransaction().getServiceID();

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY): {
                pref.edit().putString(Constants.USER_BALANCE, fireBaseNotification.getAvailable_balance() + "").apply();
                FCMPushNotificationStatusHolder.setUpdateAvailable(Constants.PUSH_NOTIFICATION_TAG_BALANCE, true);
                FCMPushNotificationStatusHolder.setUpdateAvailable(Constants.PUSH_NOTIFICATION_TAG_TRANSACTION, true);

                Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST,fireBaseNotification.getTransaction());
                Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST_NEW,fireBaseNotification.getTransaction());
            }
        }
    }
}
