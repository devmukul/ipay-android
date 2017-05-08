package bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger;

import android.util.Log;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class Logger {

    public static void logD(String tag, String message) {
        if (Constants.DEBUG)
            Log.d(tag, message);
    }

    public static void logD(String tag, String message, Exception e) {
        if (Constants.DEBUG)
            Log.d(tag, message, e);
    }

    public static void logI(String tag, String message) {
        if (Constants.DEBUG)
            Log.i(tag, message);
    }

    public static void logE(String tag, String message) {
        if (Constants.DEBUG)
            Log.e(tag, message);
    }

    public static void logW(String tag, String message) {
        if (Constants.DEBUG)
            Log.w(tag, message);
    }
}
