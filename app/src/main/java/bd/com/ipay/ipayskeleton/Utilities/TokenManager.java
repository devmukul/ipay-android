package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

/**
 * We get an authentication token from the server when the user logs in. This token needs to be
 * sent to the server with subsequent requests. It also needs to be refreshed periodically. All
 * types of token management/access should be done through this class.
 */
public class TokenManager {

    private static final String TOKEN = "TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    private static final String REFRESH_TOKEN_FETCH_TIME = "REFRESH_TOKEN_FETCH_TIME";
    
    // This field will be set when a personal user switches to an employer's account
    private static String operatingOnAccountId;

    private static String token = "";
    private static String refreshToken = "";

    //    private static CountDownTimer tokenTimer;
    private static long iPayTokenTimeInMs = Constants.DEFAULT_TOKEN_TIME;
    private static long tokenWindowOverlapTime = Constants.DEFAULT_TOKEN_OVERLAP_TIME;

    private static SharedPreferences preferences;

    public static void initialize(Context context) {
        preferences = context.getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
    }

    public static String getOperatingOnAccountId() {
        return operatingOnAccountId;
    }

    public static void setOperatingOnAccountId(String operatingOnAccountId) {
        TokenManager.operatingOnAccountId = operatingOnAccountId;
    }

    public static boolean isEmployerAccountActive() {
        return operatingOnAccountId != null && !operatingOnAccountId.isEmpty();
    }

    public static void deactivateEmployerAccount() {
        operatingOnAccountId = null;
    }

    public static boolean isTokenExists() {
        return token != null && !token.isEmpty();
    }

    public static String getToken() {
        if (TextUtils.isEmpty(token)) {
            final String encodedToken = preferences.getString(getTokenPrefKey(), "");
            if (TextUtils.isEmpty(encodedToken)) {
                token = "";
            } else {
                token = new String(Base64.decode(encodedToken, Base64.NO_WRAP));
            }
        }
        Log.d("TOKEN", token);
        return token;
    }

    public static void setToken(String token) {
        TokenManager.token = token;
        preferences.edit().putString(getTokenPrefKey(), Base64.encodeToString(token.getBytes(), Base64.NO_WRAP)).apply();
    }

    public static String getRefreshToken() {
        if (TextUtils.isEmpty(refreshToken)) {
            final String encodedRefreshToken = preferences.getString(getRefreshTokenPrefKey(), "");
            if (TextUtils.isEmpty(encodedRefreshToken)) {
                refreshToken = "";
            } else {
                refreshToken = new String(Base64.decode(encodedRefreshToken, Base64.NO_WRAP));
            }
        }
        Log.d("REFRESH TOKEN", refreshToken);
        return refreshToken;
    }

    public static void setRefreshToken(String refreshToken) {
        TokenManager.refreshToken = refreshToken;
        preferences.edit().putString(getRefreshTokenPrefKey(), Base64.encodeToString(refreshToken.getBytes(), Base64.NO_WRAP)).apply();
    }

    public static void invalidateToken() {
        setToken("");
    }

    public static void invalidateRefreshToken() {
        setRefreshToken("");
    }

    public static long getiPayTokenTimeInMs() {

        // We need to set the overlapping time for a refresh token.
        // We take one fifth of the original token time as overlapping time for now.
        tokenWindowOverlapTime = iPayTokenTimeInMs / 5;

        // If the time is less than the default overlap time, take the default one.
        if (tokenWindowOverlapTime < Constants.DEFAULT_TOKEN_OVERLAP_TIME)
            tokenWindowOverlapTime = Constants.DEFAULT_TOKEN_OVERLAP_TIME;

        // We need to call for refresh token few seconds before the server timer valid limit.
        // Server has a token window for few seconds/minutes. By default this is 15 seconds.
        // It'll serve new token in this overlapping time window before and after the token time expires.
        return iPayTokenTimeInMs - tokenWindowOverlapTime;
    }

    public static void setiPayTokenTimeInMs(long iPayTokenTimeInMs) {
        Logger.logW("Token Timer Interval", iPayTokenTimeInMs + "");
        TokenManager.iPayTokenTimeInMs = iPayTokenTimeInMs;
    }

    public static long getLastRefreshTokenFetchTime() {
        return preferences.getLong(REFRESH_TOKEN_FETCH_TIME, -1);
    }

    public static void setLastRefreshTokenFetchTime(long currentTime) {
        preferences.edit().putLong(REFRESH_TOKEN_FETCH_TIME, currentTime).apply();
    }

    public static String getTokenPrefKey() {
        return Base64.encodeToString(TOKEN.getBytes(), Base64.NO_WRAP);
    }

    public static String getRefreshTokenPrefKey() {
        return Base64.encodeToString(REFRESH_TOKEN.getBytes(), Base64.NO_WRAP);
    }
}
