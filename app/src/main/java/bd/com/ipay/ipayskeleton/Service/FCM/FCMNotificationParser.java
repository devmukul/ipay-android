package bd.com.ipay.ipayskeleton.Service.FCM;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class FCMNotificationParser {

    public static void notificationParser(Context context, FCMNotificationResponse fcmNotificationResponse) {
        int serviceID = fcmNotificationResponse.getServiceId();
        boolean isReceiver = fcmNotificationResponse.isReceiver();
        int status = fcmNotificationResponse.getNotificationData().getStatus();

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
            case (Constants.TRANSACTION_HISTORY_MAKE_PAYMENT): {
                Utilities.sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
                Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST);
            }
            case (Constants.TRANSACTION_HISTORY_REQUEST_MONEY):
            case (Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT): {
                if (status == Constants.TRANSACTION_STATUS_PROCESSING) {
                    Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST);
                    if (isReceiver)
                        Utilities.sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);

                } else if (status == Constants.TRANSACTION_STATUS_ACCEPTED) {
                    Utilities.sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
                    Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST);
                    Utilities.sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);

                } else if (status == Constants.TRANSACTION_STATUS_REJECTED || status == Constants.TRANSACTION_STATUS_CANCELLED) {
                    Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST);
                    Utilities.sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
                }
            }
        }
    }
}
