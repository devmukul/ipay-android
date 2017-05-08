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

    public static String getUserName() {
        return pref.getString(SharedPrefConstants.USER_NAME, "");
    }

    public static void setUserName(String value) {
        pref.edit().putString(SharedPrefConstants.USER_NAME, value).apply();
    }

    public static String getMobileNumber() {
        return pref.getString(SharedPrefConstants.USERID, "");
    }

    public static String getProfileImageUrl() {
        return pref.getString(Constants.PROFILE_PICTURE, "");
    }

    public static boolean isAccountVerified() {
        return getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED);
    }

    public static boolean isBusinessAccount() {
        return getAccountType(Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE;
    }

    public static String getVerificationStatus() {
        return pref.getString(SharedPrefConstants.VERIFICATION_STATUS, "");
    }

    public static int getAccountType(int defaultValue) {
        return pref.getInt(SharedPrefConstants.ACCOUNT_TYPE, defaultValue);
    }

    public static void setProfilePicture(String value) {
        pref.edit().putString(SharedPrefConstants.PROFILE_PICTURE, value).apply();
    }

    public static void setVerificationStatus(String value) {
        pref.edit().putString(SharedPrefConstants.VERIFICATION_STATUS, value).apply();
    }

    public static void setMobileNumber(String value) {
        pref.edit().putString(SharedPrefConstants.USERID, value).apply();
    }

    public static String getUUID(String defaultValue) {
        return pref.getString(SharedPrefConstants.UUID, defaultValue);
    }

    public static String getBIRTHDAY(String defaultValue) {
        return pref.getString(SharedPrefConstants.BIRTHDAY, defaultValue);
    }

    public static void setNAME(String value) {
        pref.edit().putString(SharedPrefConstants.NAME, value).apply();
    }

    public static void setUUID(String value) {
        pref.edit().putString(SharedPrefConstants.UUID, value).apply();
    }

    public static void setAccountType(int value) {
        pref.edit().putInt(SharedPrefConstants.ACCOUNT_TYPE, value).apply();
    }

    public static void setBIRTHDAY(String value) {
        pref.edit().putString(SharedPrefConstants.BIRTHDAY, value).apply();
    }

    public static void setPASSWORD(String value) {
        pref.edit().putString(SharedPrefConstants.PASSWORD, value).apply();
    }

    public static void setGENDER(String value) {
        pref.edit().putString(SharedPrefConstants.GENDER, value).apply();
    }

    public static void removeUUID() {
        pref.edit().remove(SharedPrefConstants.USERID).apply();
    }


    public static boolean ifPasswordEncrypted() {
        if (pref.getString(SharedPrefConstants.KEY_PASSWORD, "") != "")
            return true;
        return false;
    }

    public static void setLoggedInStatus(boolean loggedIn) {
        pref.edit().putBoolean(SharedPrefConstants.LOGGED_IN, loggedIn).apply();
    }

    public static boolean getLoggedInStatus(boolean defaultValue) {
        boolean loggedIn = pref.getBoolean(SharedPrefConstants.LOGGED_IN, defaultValue);
        return loggedIn;
    }

    public static boolean getFingerprintAuthenticationStatus(boolean defaultValue) {
        boolean isFingerprintAuthOn = pref.getBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, defaultValue);
        return isFingerprintAuthOn;
    }

    public static void setFingerprintAuthenticationStatus(boolean value) {
        pref.edit().putBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, value).apply();
    }

    public static void clearEncryptedPassword() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SharedPrefConstants.KEY_PASSWORD, "");
        pref.edit().putBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, false).apply();
        editor.commit();
    }

    public static void updateCache(String name, String mobileNumber, String profileImageUrl, String verificationStatus) {
        setUserName(name);
        setMobileNumber(mobileNumber);
        setProfilePicture(profileImageUrl);
        setVerificationStatus(verificationStatus);

        // Send broadcast that profile information has updated, so views showing profile information
        // (e.g. HomeFragment) could be refreshed
        Intent intent = new Intent(Constants.PROFILE_INFO_UPDATE_BROADCAST);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void updateCache(String name, String profileImageUrl, String verificationStatus) {
        setUserName(name);
        setProfilePicture(profileImageUrl);
        setVerificationStatus(verificationStatus);
        // Send broadcast that profile information has updated, so views showing profile information
        // (e.g. HomeFragment) could be refreshed
        Intent intent = new Intent(Constants.PROFILE_INFO_UPDATE_BROADCAST);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

