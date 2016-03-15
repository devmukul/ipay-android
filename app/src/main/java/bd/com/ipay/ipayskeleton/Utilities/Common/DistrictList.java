package bd.com.ipay.ipayskeleton.Utilities.Common;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;


public class DistrictList {
    public static final int[] districtIds = {
            13, 14, 16,
    };

    public static final String[] districtNames = {
            "Dhaka", "Khulna", "Rajshahi",
    };

    public static final Map<Integer, String> districtIdToNameMap = new HashMap<>();
    public static final Map<String, Integer> districtNameToIdMap = new HashMap<>();

    static {
        for (int i = 0; i < districtIds.length && i < districtNames.length; i++) {
            districtIdToNameMap.put(districtIds[i], districtNames[i]);
            districtNameToIdMap.put(districtNames[i], districtIds[i]);
        }
    }
}
