package bd.com.ipay.ipayskeleton.Utilities.ToastandLogger;

import android.util.Log;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class LoggerUtilities {

    public static void logDebug(String tag, String message) {
        if (Constants.DEBUG)
            Log.d(tag, message);
    }

    public static void logDebug(String tag, String message, Exception e) {
        if (Constants.DEBUG)
            Log.d(tag, message, e);
    }

    public static void logInfo(String tag, String message) {
        if (Constants.DEBUG)
            LoggerUtilities.logInfo(tag, message);
    }
}
