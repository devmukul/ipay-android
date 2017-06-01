package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.util.SparseBooleanArray;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BASIC_PROFILE;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_DOCUMENTS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_INFO;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.INTRODUCER;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PERSONAL_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PHOTOID;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_COMPLETENESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_INFO;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_PICTURE;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFICATION_DOCUMENT;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFIED_EMAIL;


public class ACLCacheManager {
    private static SparseBooleanArray allowedServiceArray;

    private static HashMap<String, int[]> serviceAccessMapWithString;

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

    public static boolean checkServicesAccessibilityByName(String serviceName) {
        return hasServicesAccessibility(serviceAccessMapWithString.get(serviceName));
    }

    public static void updateAllowedServiceArray(int[] serviceCodeList) {
        allowedServiceArray = new SparseBooleanArray();
        for (int serviceCode : serviceCodeList) {
            allowedServiceArray.put(serviceCode, true);
        }
        populateServiceAccessMap();
    }

    private static void populateServiceAccessMap() {
        serviceAccessMapWithString = new HashMap<>();

        serviceAccessMapWithString.put(Constants.VERIFY_BANK, new int[]{ServiceIdConstants.MANAGE_BANK_ACCOUNTS});
        serviceAccessMapWithString.put(Constants.LINK_BANK, new int[]{ServiceIdConstants.MANAGE_BANK_ACCOUNTS});

        serviceAccessMapWithString.put(BASIC_PROFILE, new int[]{ServiceIdConstants.MANAGE_PROFILE});
        serviceAccessMapWithString.put(BUSINESS_INFO, new int[]{ServiceIdConstants.SEE_BUSINESS_INFO});

        serviceAccessMapWithString.put(INTRODUCER, new int[]{ServiceIdConstants.MANAGE_INTRODUCERS});

        serviceAccessMapWithString.put(PERSONAL_ADDRESS, new int[]{ServiceIdConstants.MANAGE_ADDRESS});
        serviceAccessMapWithString.put(BUSINESS_ADDRESS, new int[]{ServiceIdConstants.MANAGE_ADDRESS});

        serviceAccessMapWithString.put(VERIFIED_EMAIL, new int[]{ServiceIdConstants.MANAGE_EMAILS});

        serviceAccessMapWithString.put(BUSINESS_DOCUMENTS, new int[]{ServiceIdConstants.SEE_BUSINESS_DOCS});
        serviceAccessMapWithString.put(VERIFICATION_DOCUMENT, new int[]{ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS});
        serviceAccessMapWithString.put(PHOTOID, new int[]{ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS});

        serviceAccessMapWithString.put(PROFILE_COMPLETENESS, new int[]{ServiceIdConstants.SEE_PROFILE_COMPLETION});
        serviceAccessMapWithString.put(PROFILE_INFO, new int[]{ServiceIdConstants.SEE_PROFILE});
        serviceAccessMapWithString.put(PROFILE_PICTURE, new int[]{ServiceIdConstants.MANAGE_PROFILE_PICTURE});
    }
}
