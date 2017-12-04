package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SharedPrefManager {
    private static SharedPreferences pref;


    public static void initialize(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static boolean getFirstLaunch(boolean defaultValue) {
        return pref.getBoolean(SharedPrefConstants.FIRST_LAUNCH, defaultValue);
    }

    public static String getUserBalance() {
        return pref.getString(SharedPrefConstants.USER_BALANCE, "0.0");
    }

    public static void setUserBalance(String value) {
        pref.edit().putString(SharedPrefConstants.USER_BALANCE, value).apply();
    }

    public static boolean isPinAdded(boolean defaultValue) {
        return pref.getBoolean(SharedPrefConstants.IS_PIN_ADDED, defaultValue);
    }

    public static int getMobileNumberType(int defaultValue) {
        return pref.getInt(SharedPrefConstants.MOBILE_NUMBER_TYPE, defaultValue);
    }

    public static void setFirstLaunch(boolean defaultValue) {
        pref.edit().putBoolean(SharedPrefConstants.FIRST_LAUNCH, defaultValue).apply();
    }

    public static void setPinAdded(boolean value) {
        pref.edit().putBoolean(SharedPrefConstants.IS_PIN_ADDED, value).apply();
    }

    public static void setMobileNumberType(int value) {
        pref.edit().putInt(SharedPrefConstants.MOBILE_NUMBER_TYPE, value).apply();
    }

    public static void setUserCountry(String value) {
        pref.edit().putString(SharedPrefConstants.USERCOUNTRY, value).apply();
    }

    public static String getUserCountry() {
        return pref.getString(SharedPrefConstants.USERCOUNTRY, "BD");
    }

    public static String getKeyPassword(String defaultValue) {
        return pref.getString(SharedPrefConstants.KEY_PASSWORD, defaultValue);
    }

    public static void setKeyPassword(String value) {
        pref.edit().putString(SharedPrefConstants.KEY_PASSWORD, value).apply();
    }

    public static String getKeyPasswordIv(String defaultValue) {
        return pref.getString(SharedPrefConstants.KEY_PASSWORD_IV, defaultValue);
    }

    public static void setKeyPasswordIv(String value) {
        pref.edit().putString(SharedPrefConstants.KEY_PASSWORD_IV, value).apply();
    }

    public static boolean ifContainsUUID() {
        return (pref.contains(SharedPrefConstants.UUID));
    }

    public static boolean ifContainsUserBalance() {
        return (pref.contains(SharedPrefConstants.USER_BALANCE));
    }

    public static boolean ifContainsUserID() {
        return (pref.contains(SharedPrefConstants.USERID));
    }

    public static boolean isBangladesh() {
        return getUserCountry().equalsIgnoreCase("BD");
    }
}
