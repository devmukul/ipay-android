package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import bd.com.ipay.ipayskeleton.Utilities.Constants;


public class ACLManager {
    private static SharedPreferences preferences;
    private static Set<String> cachedServiceIdSet;

    public static void initialize(Context context) {
        preferences = context.getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
    }

    public static boolean hasServicesAccessibility(final int... serviceCodeList) {
        if (cachedServiceIdSet == null) {
            cachedServiceIdSet = preferences.getStringSet(Constants.SERVICE_ID_SET, null);
        }

        if (cachedServiceIdSet == null || serviceCodeList == null || serviceCodeList.length == 0) {
            return false;
        }
        boolean isServiceAllowed = true;
        for (int serviceCode : serviceCodeList) {
            isServiceAllowed &= cachedServiceIdSet.contains(Integer.toString(serviceCode));
        }
        return isServiceAllowed;
    }

    public static void updateAllowedServiceArray(int[] serviceCodeList) {
        Set<String> serviceIdSet = new HashSet<>();
        for (int serviceCode : serviceCodeList) {
            serviceIdSet.add(Integer.toString(serviceCode));
        }
        preferences.edit().putStringSet(Constants.SERVICE_ID_SET, serviceIdSet).apply();
    }
}
