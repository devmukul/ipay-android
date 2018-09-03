package bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger;

import android.util.Log;

import bd.com.ipay.ipayskeleton.BuildConfig;

public class Logger {

    public static void logD(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message);
    }

    public static void logD(String tag, String message, Exception e) {
        if (BuildConfig.DEBUG)
            Log.d(tag, message, e);
    }

    public static void logI(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.i(tag, message);
    }

    public static void logE(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.e(tag, message);
    }

    public static void logW(String tag, String message) {
        if (BuildConfig.DEBUG)
            Log.w(tag, message);
    }
}
