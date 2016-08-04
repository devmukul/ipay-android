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

    private static CountDownTimer tokenTimer;
    private static long iPayTokenTimeInMs = 60000;  // By default this is one minute

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
        TokenManager.tokenTimer = tokenTimer;
    }

    public static long getiPayTokenTimeInMs() {
        return iPayTokenTimeInMs - 15000;
    }

    public static void setiPayTokenTimeInMs(long iPayTokenTimeInMs) {
        Log.w("Token Timer Interval", iPayTokenTimeInMs + "");
        TokenManager.iPayTokenTimeInMs = iPayTokenTimeInMs;
    }
}
