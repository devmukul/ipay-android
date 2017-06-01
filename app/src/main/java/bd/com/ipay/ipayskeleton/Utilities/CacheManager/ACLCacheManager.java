package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.util.SparseBooleanArray;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;
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

    private static HashMap<String, Boolean> mapServiceAccessByName;
    private static SparseBooleanArray mapServiceAccessByIntID;

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
        if (mapServiceAccessByName == null) {
            return false;
        } else if (mapServiceAccessByName.get(serviceName) == null) {
            return false;
        }
        return mapServiceAccessByName.get(serviceName);
    }

    public static boolean checkServicesAccessibilityByIntId(int id) {
        return mapServiceAccessByIntID != null && mapServiceAccessByIntID.get(id, false);
    }

    public static void updateAllowedServiceArray(int[] serviceCodeList) {
        allowedServiceArray = new SparseBooleanArray();
        for (int serviceCode : serviceCodeList) {
            allowedServiceArray.put(serviceCode, true);
        }
        populateServiceAccessByNameMapping();
        populateServiceAccessByIntIDMapping();
    }

    private static void populateServiceAccessByIntIDMapping() {
        mapServiceAccessByIntID = new SparseBooleanArray();
        mapServiceAccessByIntID.put(R.id.nav_home, true);
        mapServiceAccessByIntID.put(R.id.nav_account, hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE));
        mapServiceAccessByIntID.put(R.id.nav_bank_account, hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS));
        mapServiceAccessByIntID.put(R.id.nav_user_activity, hasServicesAccessibility(ServiceIdConstants.SEE_ACTIVITY));
        mapServiceAccessByIntID.put(R.id.nav_security_settings, hasServicesAccessibility(ServiceIdConstants.SEE_SECURITY, ServiceIdConstants.MANAGE_SECURITY));
        mapServiceAccessByIntID.put(R.id.nav_invite, hasServicesAccessibility(ServiceIdConstants.SEE_INVITATIONS));
        mapServiceAccessByIntID.put(R.id.nav_help, true);
        mapServiceAccessByIntID.put(R.id.nav_about, true);
        mapServiceAccessByIntID.put(R.id.nav_logout, hasServicesAccessibility(ServiceIdConstants.SIGN_OUT));
    }

    private static void populateServiceAccessByNameMapping() {
        mapServiceAccessByName = new HashMap<>();

        mapServiceAccessByName.put(Constants.VERIFY_BANK, hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS));
        mapServiceAccessByName.put(Constants.LINK_BANK, hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS));

        mapServiceAccessByName.put(BASIC_PROFILE, hasServicesAccessibility(ServiceIdConstants.MANAGE_PROFILE));
        mapServiceAccessByName.put(BUSINESS_INFO, hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_INFO));

        mapServiceAccessByName.put(INTRODUCER, hasServicesAccessibility(ServiceIdConstants.MANAGE_INTRODUCERS));

        mapServiceAccessByName.put(PERSONAL_ADDRESS, hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS));
        mapServiceAccessByName.put(BUSINESS_ADDRESS, hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS));

        mapServiceAccessByName.put(VERIFIED_EMAIL, hasServicesAccessibility(ServiceIdConstants.MANAGE_EMAILS));

        mapServiceAccessByName.put(BUSINESS_DOCUMENTS, hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_DOCS));
        mapServiceAccessByName.put(VERIFICATION_DOCUMENT, hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS));
        mapServiceAccessByName.put(PHOTOID, hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS));

        mapServiceAccessByName.put(PROFILE_COMPLETENESS, hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE_COMPLETION));
        mapServiceAccessByName.put(PROFILE_INFO, hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE));
        mapServiceAccessByName.put(PROFILE_PICTURE, hasServicesAccessibility(ServiceIdConstants.MANAGE_PROFILE_PICTURE));
    }
}
