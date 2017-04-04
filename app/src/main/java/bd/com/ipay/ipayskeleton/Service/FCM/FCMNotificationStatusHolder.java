package bd.com.ipay.ipayskeleton.Service.FCM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FCMNotificationStatusHolder { private static SharedPreferences pref;

    public static void initialize(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static boolean isUpdateAvailable(String tag) {
        return pref.getBoolean(tag, true);
    }

    public static void setUpdateAvailable(String tag, boolean isUpdateNeeded) {
        pref.edit().putBoolean(tag, isUpdateNeeded).apply();
    }
}
