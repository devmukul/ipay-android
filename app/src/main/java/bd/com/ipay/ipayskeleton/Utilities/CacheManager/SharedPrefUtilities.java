package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SharedPrefUtilities {

    private static SharedPreferences pref;

    public static void initialize(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        ProfileInfoCacheManager.initialize(context);
    }

    public static boolean getBoolean(String tag, boolean defaultValue) {
        return pref.getBoolean(tag, defaultValue);
    }

    public static int getInt(String tag, int defaultValue) {
        return pref.getInt(tag, defaultValue);
    }

    public static String getString(String tag, String defaultValue) {
        return pref.getString(tag, defaultValue);
    }

    public static void putBoolean(String tag, boolean value) {
        pref.edit().putBoolean(tag, value).apply();
    }

    public static void putInt(String tag, int value) {
        pref.edit().putInt(tag, value).apply();
    }

    public static void putString(String tag, String value) {
        pref.edit().putString(tag, value).apply();
    }

    public static boolean contains(String value) {
        return (pref.contains(value));
    }

    public static void remove(String value) {
        pref.edit().remove(value).apply();
    }
}
