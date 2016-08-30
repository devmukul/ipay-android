package bd.com.ipay.ipayskeleton.Utilities;

import android.os.CountDownTimer;
import android.util.Log;

/**
 * We get an authentication token from the server when the user logs in. This token needs to be
 * sent to the server with subsequent requests. It also needs to be refreshed periodically. All
 * types of token management/access should be done through this class.
 */
public class TokenManager {

    // This field will be set when a personal user switches to an employer's account
    private static String operatingOnAccountId;

    private static String token = "";
    private static String refreshToken = "";

    private static CountDownTimer tokenTimer;
    private static long iPayTokenTimeInMs = Constants.DEFAULT_TOKEN_TIME;
    private static long tokenWindowOverlapTime = Constants.DEFAULT_TOKEN_OVERLAP_TIME;

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
        return token;
    }

    public static void setToken(String token) {
        TokenManager.token = token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }

    public static void setRefreshToken(String refreshToken) {
        TokenManager.refreshToken = refreshToken;
    }

    public static CountDownTimer getTokenTimer() {
        return tokenTimer;
    }

    public static void setTokenTimer(CountDownTimer tokenTimer) {
        if (getTokenTimer() != null)
            getTokenTimer().cancel();
        TokenManager.tokenTimer = tokenTimer;
    }

    public static long getiPayTokenTimeInMs() {

        // We need to set the overlapping time for a refresh token.
        // We take one fifth of the original token time as overlapping time for now.
        tokenWindowOverlapTime = iPayTokenTimeInMs / 5;

        // We need to call for refresh token few seconds before the server timer valid limit.
        // Server has a token window for few seconds/minutes. By default this is 15 seconds.
        // It'll serve new token in this overlapping time window before and after the token time expires.
        return iPayTokenTimeInMs - tokenWindowOverlapTime;
    }

    public static void setiPayTokenTimeInMs(long iPayTokenTimeInMs) {
        if (Constants.DEBUG)
            Log.w("Token Timer Interval", iPayTokenTimeInMs + "");
        TokenManager.iPayTokenTimeInMs = iPayTokenTimeInMs;
    }
}
