package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ProfileInfoCacheManager {
    private static SharedPreferences pref;
    private static Context context;

    public static void initialize(Context context) {
        ProfileInfoCacheManager.context = context;
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static String getName() {
        return pref.getString(Constants.USER_NAME, "");
    }

    public static String getMobileNumber() {
        return pref.getString(Constants.USERID, "");
    }

    public static String getProfileImageUrl() {
        return pref.getString(Constants.PROFILE_PICTURE, "");
    }

    public static boolean isAccountVerified() {
        return getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED);
    }

    public static boolean isBusinessAccount() {
        return getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE;
    }

    public static String getVerificationStatus() {
        return pref.getString(Constants.VERIFICATION_STATUS, "");
    }

    public static int getAccountType() {
        return pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
    }

    public static void updateCache(String name, String mobileNumber, String profileImageUrl, String verificationStatus) {
        pref.edit()
                .putString(Constants.USER_NAME, name)
                .putString(Constants.USERID, mobileNumber)
                .putString(Constants.PROFILE_PICTURE, profileImageUrl)
                .putString(Constants.VERIFICATION_STATUS, verificationStatus)
                .apply();

        // Send broadcast that profile information has updated, so views showing profile information
        // (e.g. HomeFragment) could be refreshed
        Intent intent = new Intent(Constants.PROFILE_INFO_UPDATE_BROADCAST);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void updateCache(String name, String profileImageUrl, String verificationStatus) {
        pref.edit()
                .putString(Constants.USER_NAME, name)
                .putString(Constants.PROFILE_PICTURE, profileImageUrl)
                .putString(Constants.VERIFICATION_STATUS, verificationStatus)
                .apply();

        // Send broadcast that profile information has updated, so views showing profile information
        // (e.g. HomeFragment) could be refreshed
        Intent intent = new Intent(Constants.PROFILE_INFO_UPDATE_BROADCAST);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

