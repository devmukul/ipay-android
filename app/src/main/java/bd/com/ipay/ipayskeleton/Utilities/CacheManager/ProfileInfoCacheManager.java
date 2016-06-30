package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ProfileInfoCacheManager {
    private SharedPreferences pref;

    public ProfileInfoCacheManager(Context context) {
        pref = context.getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
    }

    public String getName() {
        return pref.getString(Constants.USER_NAME, "");
    }

    public String getMobileNumber() {
        return pref.getString(Constants.USERID, "");
    }

    public String getProfileImageUrl() {
        return pref.getString(Constants.PROFILE_PICTURE, "");
    }

    public String getVerificationStatus() {
        return pref.getString(Constants.VERIFICATION_STATUS, "");
    }

    public int getAccountType() {
        return pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
    }

    public void updateCache(String name, String mobileNumber, String profileImageUrl, String verificationStatus) {
        pref.edit()
                .putString(Constants.USER_NAME, name)
                .putString(Constants.USERID, mobileNumber)
                .putString(Constants.PROFILE_PICTURE, profileImageUrl)
                .putString(Constants.VERIFICATION_STATUS, verificationStatus)
                .apply();
    }

    public void updateCache(String name, String profileImageUrl, String verificationStatus) {
        pref.edit()
                .putString(Constants.USER_NAME, name)
                .putString(Constants.PROFILE_PICTURE, profileImageUrl)
                .putString(Constants.VERIFICATION_STATUS, verificationStatus)
                .apply();
    }

    public void updateCache(String mobileNumber, int accountType) {
        pref.edit()
                .putString(Constants.USERID, mobileNumber)
                .putInt(Constants.ACCOUNT_TYPE, accountType)
                .apply();
    }
}

