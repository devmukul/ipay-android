package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import bd.com.ipay.ipayskeleton.BroadcastReceiverClass.BroadcastServiceIntent;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee.GetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetProfileInfoResponse;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ProfileInfoCacheManager {

    private static SharedPreferences pref;
    private static Context context;

    public static void initialize(Context context) {
        ProfileInfoCacheManager.context = context;
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public static boolean isAccountVerified() {
        return getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED);
    }

    public static boolean isBusinessAccount() {
        return getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE;
    }

    public static String getPushNotificationToken(String defaultValue) {
        return pref.getString(SharedPrefConstants.PUSH_NOTIFICATION_TOKEN, defaultValue);
    }

    public static void setPushNotificationToken(String value) {
        pref.edit().putString(SharedPrefConstants.PUSH_NOTIFICATION_TOKEN, value).apply();
    }

    /**
     * @return For Account Type {@link Constants.PERSONAL_ACCOUNT_TYPE} Returns the User Full Name,
     * For {@link Constants.BUSINESS_ACCOUNT_TYPE} Returns the Name of the Business.
     */
    public static String getUserName() {
        return pref.getString(SharedPrefConstants.USER_NAME, "");
    }

    /**
     * @param value For Account Type {@link Constants.PERSONAL_ACCOUNT_TYPE} value have to be the User Full Name,
     *              For {@link Constants.BUSINESS_ACCOUNT_TYPE} value have to be the Name of the Business.
     */
    public static void setUserName(String value) {
        pref.edit().putString(SharedPrefConstants.USER_NAME, value).apply();
    }

    /**
     * @return iPay Account Number
     */
    public static String getMobileNumber() {
        return pref.getString(SharedPrefConstants.USERID, "");
    }

    /**
     * @param value iPay Account Number
     */
    public static void setMobileNumber(String value) {
        pref.edit().putString(SharedPrefConstants.USERID, value).apply();
    }

    /**
     * @return Relative URL for the Profile Picture
     */
    public static String getProfileImageUrl() {
        return pref.getString(Constants.PROFILE_PICTURE, "");
    }

    /**
     * @param value Save the relative URL of the Profile Picture
     */
    public static void setProfilePictureUrl(String value) {
        pref.edit().putString(SharedPrefConstants.PROFILE_PICTURE, value).apply();
    }

    public static String getPrimaryEmail() {
        return pref.getString(Constants.PRIMARY_EMAIL, "N/A");
    }

    public static void setPrimaryEmail(String value) {
        pref.edit().putString(Constants.PRIMARY_EMAIL, !TextUtils.isEmpty(value) ? value : "N/A").apply();
    }

    public static long getSignupTime() {
        return pref.getLong(Constants.SIGNUP_TIME, 0L);
    }

    public static void setSignupTime(long value) {
        pref.edit().putLong(Constants.SIGNUP_TIME, value).apply();
    }

    public static int getAccountId() {
        return pref.getInt(Constants.ACCOUNT_ID, -1);
    }

    public static void setAccountId(int value) {
        pref.edit().putInt(Constants.ACCOUNT_ID, value).apply();
    }

    public static String getVerificationStatus() {
        return pref.getString(SharedPrefConstants.VERIFICATION_STATUS, "");
    }

    public static void setVerificationStatus(String value) {
        pref.edit().putString(SharedPrefConstants.VERIFICATION_STATUS, value).apply();
    }

    public static int getAccountType() {
        return pref.getInt(SharedPrefConstants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
    }

    public static void setAccountType(int value) {
        pref.edit().putInt(SharedPrefConstants.ACCOUNT_TYPE, value).apply();
    }

    public static String getUUID() {
        return pref.getString(SharedPrefConstants.UUID, null);
    }

    public static String getBirthday() {
        return pref.getString(SharedPrefConstants.BIRTHDAY, "");
    }

    public static void setName(String value) {
        pref.edit().putString(SharedPrefConstants.NAME, value).apply();
    }

    public static void setUUID(String value) {
        pref.edit().putString(SharedPrefConstants.UUID, value).apply();
    }

    public static void setBirthday(String value) {
        pref.edit().putString(SharedPrefConstants.BIRTHDAY, value).apply();
    }

    public static void setGender(String value) {
        pref.edit().putString(SharedPrefConstants.GENDER, value).apply();
    }

    public static void removeUUID() {
        pref.edit().remove(SharedPrefConstants.UUID).apply();
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
        editor.apply();
    }

    public static void updateProfileInfoCache(GetProfileInfoResponse profileInfo) {
        // For Business Account It contains the name of the contact person rather than the name of the business.
        // So We have to check if the Account Type is Personal or not to save this information
        if (!ProfileInfoCacheManager.isBusinessAccount())
            setUserName(profileInfo.getName());

        setProfilePictureUrl(Utilities.getImage(profileInfo.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH));
        setVerificationStatus(profileInfo.getVerificationStatus());
        setPrimaryEmail(profileInfo.getPrimaryEmail());
        setAccountId(profileInfo.getAccountId());
        setSignupTime(profileInfo.getSignupTime());

        BroadcastServiceIntent.sendBroadcast(context, Constants.PROFILE_INFO_UPDATE_BROADCAST);
    }

    public static void updateBusinessInfoCache(GetBusinessInformationResponse businessInfo) {
        setUserName(businessInfo.getBusinessName());
        setProfilePictureUrl(Utilities.getImage(businessInfo.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH));
        setVerificationStatus(businessInfo.getVerificationStatus());

        BroadcastServiceIntent.sendBroadcast(context, Constants.PROFILE_INFO_UPDATE_BROADCAST);
    }
}

