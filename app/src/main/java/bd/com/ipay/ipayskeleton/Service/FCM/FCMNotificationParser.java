package bd.com.ipay.ipayskeleton.Service.FCM;

import android.content.Context;

import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.MultipleBroadCastServiceIntent;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.FCMNotificationResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FCMNotificationParser {

    public static int parseServiceID(FCMNotificationResponse fcmNotificationResponse) {
        return fcmNotificationResponse.getServiceId();
    }

    public static void parseInAppNotification(Context context, FCMNotificationResponse fcmNotificationResponse) {
        int serviceID = fcmNotificationResponse.getServiceId();
        boolean isReceiver = fcmNotificationResponse.isReceiver();

        try {
            if (fcmNotificationResponse.getNotificationData() != null) {
                int status = fcmNotificationResponse.getNotificationData().getStatus();

                switch (serviceID) {
                    case (Constants.TRANSACTION_HISTORY_SEND_MONEY):
                    case (Constants.TRANSACTION_HISTORY_MAKE_PAYMENT): {
                        MultipleBroadCastServiceIntent.sendBroadcastForPaymentOrSendMoney(context);
                        break;
                    }
                    case (Constants.TRANSACTION_HISTORY_REQUEST_MONEY):
                    case (Constants.TRANSACTION_HISTORY_REQUEST_PAYMENT): {
                        if (status == Constants.TRANSACTION_STATUS_PROCESSING) {
                            MultipleBroadCastServiceIntent.sendBroadcastForPendingMoneyOrPaymentRequest(context, isReceiver);

                        } else if (status == Constants.TRANSACTION_STATUS_ACCEPTED) {
                            MultipleBroadCastServiceIntent.sendBroadcastForAcceptedMoneyOrPaymentRequest(context);

                        } else if (status == Constants.TRANSACTION_STATUS_REJECTED || status == Constants.TRANSACTION_STATUS_CANCELLED) {
                            MultipleBroadCastServiceIntent.sendBroadcastForCanceledOrRejectedRequest(context);
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
