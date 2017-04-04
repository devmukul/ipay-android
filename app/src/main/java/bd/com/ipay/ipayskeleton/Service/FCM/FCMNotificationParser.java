package bd.com.ipay.ipayskeleton.Service.FCM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class FCMNotificationParser {

    public static void notificationParser(Context context, int serviceID) {;

        switch (serviceID) {
            case (Constants.TRANSACTION_HISTORY_SEND_MONEY): {
                Utilities.sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
                Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST);
            }
            case (Constants.TRANSACTION_HISTORY_MAKE_PAYMENT): {
                Utilities.sendBroadcast(context, Constants.BALANCE_UPDATE_BROADCAST);
                Utilities.sendBroadcast(context, Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST);
            }


        }
    }
}
