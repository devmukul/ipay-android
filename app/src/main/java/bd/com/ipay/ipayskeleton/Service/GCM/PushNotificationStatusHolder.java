package bd.com.ipay.ipayskeleton.Service.GCM;

import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefUtilities;

public class PushNotificationStatusHolder {

    public static boolean isUpdateNeeded(String tag) {
        return SharedPrefUtilities.getBoolean(tag, true);
    }

    public static void setUpdateNeeded(String tag, boolean isUpdateNeeded) {
      SharedPrefUtilities.putBoolean(tag, isUpdateNeeded);
    }
}
