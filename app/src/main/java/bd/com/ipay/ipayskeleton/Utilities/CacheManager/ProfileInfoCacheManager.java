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
    @SuppressWarnings("JavadocReference")
    public static String getUserName() {
        return pref.getString(SharedPrefConstants.USER_NAME, "");
    }

    /**
     * @param value For Account Type {@link Constants.PERSONAL_ACCOUNT_TYPE} value have to be the User Full Name,
     *              For {@link Constants.BUSINESS_ACCOUNT_TYPE} value have to be the Name of the Business.
     */
    @SuppressWarnings("JavadocReference")
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

    public static void setUUID(String value) {
        pref.edit().putString(SharedPrefConstants.UUID, value).apply();
    }

    public static String getBirthday() {
        return pref.getString(SharedPrefConstants.BIRTHDAY, "");
    }

    public static void setBirthday(String value) {
        pref.edit().putString(SharedPrefConstants.BIRTHDAY, value).apply();
    }

    public static void setName(String value) {
        pref.edit().putString(SharedPrefConstants.NAME, value).apply();
    }

    public static String getName() {
        return pref.getString(SharedPrefConstants.NAME, "");
    }

    public static String getGender() {
        return pref.getString(SharedPrefConstants.GENDER, null);
    }

    public static void setGender(String value) {
        pref.edit().putString(SharedPrefConstants.GENDER, value).apply();
    }

    public static void removeUUID() {
        pref.edit().remove(SharedPrefConstants.UUID).apply();
    }

    public static boolean ifPasswordEncrypted() {
        return !pref.getString(SharedPrefConstants.KEY_PASSWORD, "").equals("");
    }

    public static void setLoggedInStatus(boolean loggedIn) {
        pref.edit().putBoolean(SharedPrefConstants.LOGGED_IN, loggedIn).apply();
    }

    public static boolean getLoggedInStatus(boolean defaultValue) {
        boolean loggedIn = pref.getBoolean(SharedPrefConstants.LOGGED_IN, defaultValue);
        return loggedIn;
    }

    public static boolean getFingerprintAuthenticationStatus() {
        boolean isFingerprintAuthOn = pref.getBoolean(Constants.IS_FINGERPRINT_AUTHENTICATION_ON, false);
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
        if (profileInfo != null) {
            if (!ProfileInfoCacheManager.isBusinessAccount())
                setUserName(profileInfo.getName());

            setProfilePictureUrl(Utilities.getImage(profileInfo.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH));
            setVerificationStatus(profileInfo.getVerificationStatus());
            setPrimaryEmail(profileInfo.getPrimaryEmail());
            setAccountId(profileInfo.getAccountId());
            setSignupTime(profileInfo.getSignupTime());
            setGender(profileInfo.getGender());
            setBirthday(profileInfo.getDob());
            setMobileNumber(profileInfo.getMobileNumber());
            setAccountType(profileInfo.getAccountType());
        }

        BroadcastServiceIntent.sendBroadcast(context, Constants.PROFILE_INFO_UPDATE_BROADCAST);
    }

    public static void updateBusinessInfoCache(GetBusinessInformationResponse businessInfo) {
        if (businessInfo != null) {
            setUserName(businessInfo.getBusinessName());
            setProfilePictureUrl(Utilities.getImage(businessInfo.getProfilePictures(), Constants.IMAGE_QUALITY_HIGH));
            setVerificationStatus(businessInfo.getVerificationStatus());
        } else {
            setUserName("");
            setProfilePictureUrl("");
            setVerificationStatus("");
        }

        BroadcastServiceIntent.sendBroadcast(context, Constants.PROFILE_INFO_UPDATE_BROADCAST);
    }

    public static void setOnBoardAllStepsDoneStatus(boolean isAllOnBoardStepsDone) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.ON_BOARD_STEPS_DONE, isAllOnBoardStepsDone).apply();
    }

    public static boolean isAnyOnBoardStepSkipped() {
        return !pref.getBoolean(SharedPrefConstants.ON_BOARD_STEPS_DONE, false);
    }

    public static void uploadProfilePicture(boolean defaultvalue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.PROFILE_PICTURE_UPLOADED, defaultvalue).apply();
    }

    public static boolean isProfilePictureUploaded() {
        return pref.getBoolean(SharedPrefConstants.PROFILE_PICTURE_UPLOADED, false);
    }

    public static void uploadIdentificationDocument(boolean defaultvalue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.IDENTIFICATION_DOCUMENT_UPLOADED, defaultvalue).apply();
    }

    public static boolean isIdentificationDocumentUploaded() {
        return pref.getBoolean(SharedPrefConstants.IDENTIFICATION_DOCUMENT_UPLOADED, false);
    }

    public static void addBasicInfo(boolean defaultvalue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.BASIC_INFO_ADDED, defaultvalue).apply();
    }

    public static void addSourceOfFund(boolean defaultValue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.SOURCE_OF_FUND_ADDED, defaultValue).apply();
    }

    public static boolean isSourceOfFundAdded() {
        return pref.getBoolean(SharedPrefConstants.SOURCE_OF_FUND_ADDED, false);
    }

    public static boolean isBasicInfoAdded() {
        return pref.getBoolean(SharedPrefConstants.BASIC_INFO_ADDED, false);
    }

    public static void addBankInfo(boolean defaultvalue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.BANK_INFO_ADDED, defaultvalue).apply();
    }

    public static boolean isBankInfoAdded() {
        return pref.getBoolean(SharedPrefConstants.BANK_INFO_ADDED, false);
    }

    public static void askForIntroduction(boolean defaultvalue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.INTRODUCTION_ASKED, defaultvalue).apply();
    }

    public static boolean isIntroductionAsked() {
        return pref.getBoolean(SharedPrefConstants.INTRODUCTION_ASKED, false);
    }

    public static void switchedFromSignup(boolean defaultvalue) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.SWITCHED_FROM_SIGNUP, defaultvalue).apply();
    }

    public static boolean isSwitchedFromSignup() {
        return pref.getBoolean(SharedPrefConstants.SWITCHED_FROM_SIGNUP, false);
    }

    public static boolean isAccountSwitched() {
        return pref.getBoolean(SharedPrefConstants.IS_ACCOUNT_SWITCHED, false);
    }

    public static void setSwitchAccount(boolean accountSwitch) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(SharedPrefConstants.IS_ACCOUNT_SWITCHED, accountSwitch).apply();
    }

    public static void saveMainUserProfileInfo(String profileInfo) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SharedPrefConstants.PROFILE_INFO, profileInfo).apply();
    }

    public static String getMainUserProfileInfo() {
        String profileInfoString = pref.getString(SharedPrefConstants.PROFILE_INFO, null);
        return profileInfoString;
    }

    public static void saveMainUserBusinessInfo(String businessInformationResponse) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SharedPrefConstants.PROFILE_INFO_BUSINESS, businessInformationResponse).apply();
    }

    public static String getMainUserBusinessInfo() {
        return pref.getString(SharedPrefConstants.PROFILE_INFO_BUSINESS, null);
    }

    public static void setOnAccountId(String onAccountId) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(Constants.OPERATING_ON_ACCOUNT_ID, onAccountId).apply();
    }

    public static String getOnAccountId() {
        return pref.getString(Constants.OPERATING_ON_ACCOUNT_ID, "");
    }

    public static void setId(long id) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(SharedPrefConstants.Id, id).apply();
    }

    public static long getId() {
        return pref.getLong(SharedPrefConstants.Id, 0);
    }
}

