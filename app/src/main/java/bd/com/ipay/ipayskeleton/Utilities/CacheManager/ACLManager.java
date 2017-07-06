package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.util.SparseBooleanArray;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BASIC_PROFILE;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_DOCUMENTS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_INFO;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.INTRODUCER;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.LINK_AND_VERIFY_BANK;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PARENT;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PERSONAL_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PHOTOID;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_COMPLETENESS;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_INFO;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_PICTURE;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFICATION_DOCUMENT;
import static bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFIED_EMAIL;
import static bd.com.ipay.ipayskeleton.Utilities.Constants.LINK_BANK;
import static bd.com.ipay.ipayskeleton.Utilities.Constants.VERIFY_BANK;


public class ACLManager {
    private static SparseBooleanArray allowedServiceArray;

    private static HashMap<String, Boolean> mapServiceAccessByTargetedFragment;
    private static SparseBooleanArray mapServiceAccessByNavigationMenuId;

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

    public static boolean checkServicesAccessibilityByTargetedFragment(String serviceName) {
        if (mapServiceAccessByTargetedFragment == null) {
            return false;
        } else if (mapServiceAccessByTargetedFragment.get(serviceName) == null) {
            return false;
        }
        return mapServiceAccessByTargetedFragment.get(serviceName);
    }

    public static boolean checkServicesAccessibilityByNavigationMenuId(int id) {
        return mapServiceAccessByNavigationMenuId != null && mapServiceAccessByNavigationMenuId.get(id, false);
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
        mapServiceAccessByNavigationMenuId = new SparseBooleanArray();

        //Following menu will have access by default. Currently these options/menu don't require any access control.
        mapServiceAccessByNavigationMenuId.put(R.id.nav_home, true);
        mapServiceAccessByNavigationMenuId.put(R.id.nav_account, true);
        mapServiceAccessByNavigationMenuId.put(R.id.nav_security_settings, true);
        mapServiceAccessByNavigationMenuId.put(R.id.nav_live_chat, true);
        mapServiceAccessByNavigationMenuId.put(R.id.nav_help, true);
        mapServiceAccessByNavigationMenuId.put(R.id.nav_about, true);

        //Following menu will require access control. These will be populated from the access control list.
        mapServiceAccessByNavigationMenuId.put(R.id.nav_bank_account, hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS));
        mapServiceAccessByNavigationMenuId.put(R.id.nav_user_activity, hasServicesAccessibility(ServiceIdConstants.SEE_ACTIVITY));
        mapServiceAccessByNavigationMenuId.put(R.id.nav_invite, hasServicesAccessibility(ServiceIdConstants.SEE_INVITATIONS, ServiceIdConstants.MANAGE_INVITATIONS));
        mapServiceAccessByNavigationMenuId.put(R.id.nav_logout, hasServicesAccessibility(ServiceIdConstants.SIGN_OUT));
    }

    private static void populateServiceAccessByNameMapping() {
        mapServiceAccessByTargetedFragment = new HashMap<>();

        mapServiceAccessByTargetedFragment.put(VERIFY_BANK, hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS));
        mapServiceAccessByTargetedFragment.put(LINK_BANK, hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS));
        mapServiceAccessByTargetedFragment.put(LINK_AND_VERIFY_BANK, hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS));
        mapServiceAccessByTargetedFragment.put(PARENT, hasServicesAccessibility(ServiceIdConstants.SEE_PARENT));

        mapServiceAccessByTargetedFragment.put(BASIC_PROFILE, hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE));
        mapServiceAccessByTargetedFragment.put(BUSINESS_INFO, hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_INFO));

        mapServiceAccessByTargetedFragment.put(INTRODUCER, hasServicesAccessibility(ServiceIdConstants.MANAGE_INTRODUCERS));

        mapServiceAccessByTargetedFragment.put(PERSONAL_ADDRESS, hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS));
        mapServiceAccessByTargetedFragment.put(BUSINESS_ADDRESS, hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS));

        mapServiceAccessByTargetedFragment.put(VERIFIED_EMAIL, hasServicesAccessibility(ServiceIdConstants.MANAGE_EMAILS));

        mapServiceAccessByTargetedFragment.put(BUSINESS_DOCUMENTS, hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_DOCS));
        mapServiceAccessByTargetedFragment.put(VERIFICATION_DOCUMENT, hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS));
        mapServiceAccessByTargetedFragment.put(PHOTOID, hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS));

        mapServiceAccessByTargetedFragment.put(PROFILE_COMPLETENESS, hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE_COMPLETION));
        mapServiceAccessByTargetedFragment.put(PROFILE_INFO, true);
        mapServiceAccessByTargetedFragment.put(PROFILE_PICTURE, hasServicesAccessibility(ServiceIdConstants.MANAGE_PROFILE_PICTURE));
    }
}
