package bd.com.ipay.ipayskeleton.Service.FCM;

import android.content.Context;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Notification.NotificationUtilities;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class FCMNotificationParser {

    public static void notificationParser(Context context, FCMNotificationResponse fcmNotificationResponse) {
        int serviceID = fcmNotificationResponse.getServiceId();
        boolean isReceiver = fcmNotificationResponse.isReceiver();
        int status = fcmNotificationResponse.getNotificationData().getStatus();

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
            case (Constants.TRANSACTION_HISTORY_MAKE_PAYMENT): {
                sendBroadcastForPaymentOrSendMoney(context);
                break;
            }
            case (Constants.TRANSACTION_HISTORY_REQUEST_MONEY):
            case (Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT): {
                if (status == Constants.TRANSACTION_STATUS_PROCESSING) {
                    sendBroadcastForPendingMoneyOrPaymentRequest(context, isReceiver);

                } else if (status == Constants.TRANSACTION_STATUS_ACCEPTED) {
                    sendBroadcastForAcceptedMoneyOrPaymentRequest(context);

                } else if (status == Constants.TRANSACTION_STATUS_REJECTED || status == Constants.TRANSACTION_STATUS_CANCELLED) {
                    sendBroadcastForCanceledOrRejectedRequest(context);
                }
                break;
            }
        }
    }

    private static void sendBroadcastForPaymentOrSendMoney(Context context) {
        NotificationUtilities.sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
        NotificationUtilities.sendBroadcast(context, Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST);
    }

    private static void sendBroadcastForPendingMoneyOrPaymentRequest(Context context, boolean isReceiver) {
        NotificationUtilities.sendBroadcast(context, Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        if (isReceiver)
            NotificationUtilities.sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
    }

    private static void sendBroadcastForAcceptedMoneyOrPaymentRequest(Context context) {
        NotificationUtilities.sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
        NotificationUtilities.sendBroadcast(context, Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        NotificationUtilities.sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
    }

    private static void sendBroadcastForCanceledOrRejectedRequest(Context context) {
        NotificationUtilities.sendBroadcast(context, Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        NotificationUtilities.sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
    }
}
