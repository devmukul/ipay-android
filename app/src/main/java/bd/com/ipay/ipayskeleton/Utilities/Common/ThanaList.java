package bd.com.ipay.ipayskeleton.Utilities.Common;


import java.util.HashMap;
import java.util.Map;


public class ThanaList {
    public static final int[] thanaIds = {
            12, 14,
    };

    public static final String[] thanaNames = {
            "Gulshan", "Dhanmondi",
    };

    public static final Map<Integer, String> thanaIdToNameMap = new HashMap<>();
    public static final Map<String, Integer> thanaNameToIdMap = new HashMap<>();

    static {
        for (int i = 0; i < thanaIds.length && i < thanaNames.length; i++) {
            thanaIdToNameMap.put(thanaIds[i], thanaNames[i]);
            thanaNameToIdMap.put(thanaNames[i], thanaIds[i]);
        }
    }
}
