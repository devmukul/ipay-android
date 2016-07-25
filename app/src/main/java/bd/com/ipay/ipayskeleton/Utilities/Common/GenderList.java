package bd.com.ipay.ipayskeleton.Utilities.Common;


import java.util.HashMap;
import java.util.Map;


public class GenderList {
    private static final String[] genderCodes = {
            "M", "F", "O"
    };

    public static final String[] genderNames = {
            "Male", "Female", "Other"
    };

    public static final Map<String, String> genderCodeToNameMap = new HashMap<>();
    public static final Map<String, String> genderNameToCodeMap = new HashMap<>();

    static {
        for (int i = 0; i < genderCodes.length && i < genderNames.length; i++) {
            genderCodeToNameMap.put(genderCodes[i], genderNames[i]);
            genderNameToCodeMap.put(genderNames[i], genderCodes[i]);
        }
    }
}
