package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.util.SparseBooleanArray;

/**
 * Created by sajid.shahriar on 5/31/17.
 */

public class ACLCacheManager {
    private static SparseBooleanArray allowedServiceArray;

    public static void initialize() {
        allowedServiceArray = new SparseBooleanArray();
    }

    public static boolean hasServicesAccessibility(final int... serviceCodeList) {
        if (allowedServiceArray == null || serviceCodeList == null || serviceCodeList.length == 0) {
            return false;
        }
        boolean isServiceAllowed = true;
        for (int serviceCode : serviceCodeList) {
            isServiceAllowed &= allowedServiceArray.get(serviceCode, false);
        }
        return isServiceAllowed;
    }

    public static void updateAllowedServiceArray(int[] serviceCodeList) {
        allowedServiceArray = new SparseBooleanArray();
        for (int serviceCode : serviceCodeList) {
            allowedServiceArray.put(serviceCode, true);
        }
    }
}
