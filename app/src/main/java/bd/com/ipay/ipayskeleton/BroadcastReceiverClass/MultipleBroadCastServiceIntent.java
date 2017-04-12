package bd.com.ipay.ipayskeleton.BroadcastReceiverClass;

import android.content.Context;

import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.BroadcastServiceIntent;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MultipleBroadCastServiceIntent extends BroadcastServiceIntent {

    public static void sendBroadcastForPaymentOrSendMoney(Context context) {
        sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
        sendBroadcast(context, Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST);
    }

    public static void sendBroadcastForPendingMoneyOrPaymentRequest(Context context, boolean isReceiver) {
        sendBroadcast(context, Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        if (isReceiver)
            sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
    }

    public static void sendBroadcastForAcceptedMoneyOrPaymentRequest(Context context) {
        sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
        sendBroadcast(context, Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        sendBroadcast(context, Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
    }

    public static void sendBroadcastForCanceledOrRejectedRequest(Context context) {
        sendBroadcast(context, Constants.COMPLETED_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        sendBroadcast(context, Constants.PENDING_TRANSACTION_HISTORY_UPDATE_BROADCAST);
        sendBroadcast(context, Constants.NOTIFICATION_UPDATE_BROADCAST);
    }
}
