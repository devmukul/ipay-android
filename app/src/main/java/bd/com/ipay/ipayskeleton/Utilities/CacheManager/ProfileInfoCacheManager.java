package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ProfileInfoCacheManager {
    private static Context context;

    public static void initialize(Context context) {
        ProfileInfoCacheManager.context = context;
    }

    public static String getName() {
        return SharedPrefUtilities.getString(Constants.USER_NAME, "");
    }

    public static String getMobileNumber() {
        return SharedPrefUtilities.getString(Constants.USERID, "");
    }

    public static String getProfileImageUrl() {
        return SharedPrefUtilities.getString(Constants.PROFILE_PICTURE, "");
    }

    public static boolean isAccountVerified() {
        return getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED);
    }

    public static boolean isBusinessAccount() {
        return getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE;
    }

    public static String getVerificationStatus() {
        return SharedPrefUtilities.getString(Constants.VERIFICATION_STATUS, "");
    }

    public static int getAccountType() {
        return SharedPrefUtilities.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
    }

    public static void updateCache(String name, String mobileNumber, String profileImageUrl, String verificationStatus) {

        SharedPrefUtilities.putString(Constants.USER_NAME, name);
        SharedPrefUtilities.putString(Constants.USERID, mobileNumber);
        SharedPrefUtilities.putString(Constants.PROFILE_PICTURE, profileImageUrl);
        SharedPrefUtilities.putString(Constants.VERIFICATION_STATUS, verificationStatus);

        // Send broadcast that profile information has updated, so views showing profile information
        // (e.g. HomeFragment) could be refreshed
        Intent intent = new Intent(Constants.PROFILE_INFO_UPDATE_BROADCAST);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void updateCache(String name, String profileImageUrl, String verificationStatus) {
        SharedPrefUtilities.putString(Constants.USER_NAME, name);
        SharedPrefUtilities.putString(Constants.PROFILE_PICTURE, profileImageUrl);
        SharedPrefUtilities.putString(Constants.VERIFICATION_STATUS, verificationStatus);

        // Send broadcast that profile information has updated, so views showing profile information
        // (e.g. HomeFragment) could be refreshed
        Intent intent = new Intent(Constants.PROFILE_INFO_UPDATE_BROADCAST);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void setLoggedInStatus(boolean loggedIn) {
        SharedPrefUtilities.putBoolean(Constants.LOGGED_IN, loggedIn);
    }

    public static boolean getLoggedInStatus(boolean defaultValue) {
        boolean loggedIn = SharedPrefUtilities.getBoolean(Constants.LOGGED_IN, defaultValue);
        return loggedIn;
    }

    public static boolean getFingerprintAuthenticationStatus(boolean defaultValue) {
        boolean isFingerprintAuthOn = SharedPrefUtilities.getBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, defaultValue);
        return isFingerprintAuthOn;
    }

    public static void setFingerprintAuthenticationStatus(boolean value) {
        SharedPrefUtilities.putBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, value);
    }

    public static boolean ifPasswordEncrypted() {
        if (SharedPrefUtilities.getString(Constants.KEY_PASSWORD, "") != "")
            return true;
        return false;
    }

    public static void clearEncryptedPassword() {
        SharedPrefUtilities.putString(Constants.KEY_PASSWORD, "");
        SharedPrefUtilities.putBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, false);
    }

}

