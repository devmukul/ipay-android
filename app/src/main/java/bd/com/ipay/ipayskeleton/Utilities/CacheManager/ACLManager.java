package bd.com.ipay.ipayskeleton.Utilities.CacheManager;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
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
    private static SharedPreferences preferences;

    public static void initialize(Context context) {
        preferences = context.getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
    }

    public static boolean hasServicesAccessibility(final int... serviceCodeList) {
        Set<String> serviceIdSet = preferences.getStringSet(Constants.SERVICE_ID_SET, null);
        if (serviceIdSet == null || serviceCodeList == null || serviceCodeList.length == 0) {
            return false;
        }
        boolean isServiceAllowed = true;
        for (int serviceCode : serviceCodeList) {
            isServiceAllowed &= serviceIdSet.contains(Integer.toString(serviceCode));
        }
        return isServiceAllowed;
    }

    public static boolean checkServicesAccessibilityByTargetedFragment(String serviceName) {
        Set<String> fragmentAccessServiceIdSet = preferences.getStringSet(Constants.FRAGMENT_SERVICE_ACCESS_SET, null);

        return fragmentAccessServiceIdSet != null && fragmentAccessServiceIdSet.contains(serviceName);
    }

    public static boolean checkServicesAccessibilityByNavigationMenuId(int id) {
        Set<String> navigationMenuServiceIdSet = preferences.getStringSet(Constants.NAVIGATION_MENU_SERVICE_ACCESS_SET, null);
        return navigationMenuServiceIdSet != null && navigationMenuServiceIdSet.contains(Integer.toString(id));
    }

    public static void updateAllowedServiceArray(int[] serviceCodeList) {
        Set<String> serviceIdSet = new HashSet<>();
        for (int serviceCode : serviceCodeList) {
            serviceIdSet.add(Integer.toString(serviceCode));
        }
        preferences.edit().putStringSet(Constants.SERVICE_ID_SET, serviceIdSet).apply();
        populateServiceAccessByNameMapping();
        populateServiceAccessByIntIDMapping();
    }

    private static void populateServiceAccessByIntIDMapping() {
        Set<String> navigationMenuServiceIdSet = new HashSet<>();

        //Following menu will have access by default. Currently these options/menu don't require any access control.
        navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_home));
        navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_account));
        navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_security_settings));
        navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_live_chat));
        navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_help));
        navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_about));

        //Following menu will require access control. These will be populated from the access control list.
        if (hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS)) {
            navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_bank_account));
        }
        if (hasServicesAccessibility(ServiceIdConstants.SEE_ACTIVITY)) {
            navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_user_activity));
        }
        if (hasServicesAccessibility(ServiceIdConstants.SEE_INVITATIONS, ServiceIdConstants.MANAGE_INVITATIONS)) {
            navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_invite));
        }
        if (hasServicesAccessibility(ServiceIdConstants.SIGN_OUT)) {
            navigationMenuServiceIdSet.add(Integer.toString(R.id.nav_logout));
        }

        preferences.edit().putStringSet(Constants.NAVIGATION_MENU_SERVICE_ACCESS_SET, navigationMenuServiceIdSet).apply();

    }

    private static void populateServiceAccessByNameMapping() {
        Set<String> fragmentAccessServiceIdSet = new HashSet<>();

        fragmentAccessServiceIdSet.add(PROFILE_INFO);

        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS)) {
            fragmentAccessServiceIdSet.add(VERIFY_BANK);
        }
        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS)) {
            fragmentAccessServiceIdSet.add(LINK_BANK);
        }
        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS)) {
            fragmentAccessServiceIdSet.add(LINK_AND_VERIFY_BANK);
        }
        if (hasServicesAccessibility(ServiceIdConstants.SEE_PARENT)) {
            fragmentAccessServiceIdSet.add(PARENT);
        }

        if (hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE)) {
            fragmentAccessServiceIdSet.add(BASIC_PROFILE);
        }
        if (hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_INFO)) {
            fragmentAccessServiceIdSet.add(BUSINESS_INFO);
        }

        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_INTRODUCERS)) {
            fragmentAccessServiceIdSet.add(INTRODUCER);
        }

        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS)) {
            fragmentAccessServiceIdSet.add(PERSONAL_ADDRESS);
        }
        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_ADDRESS)) {
            fragmentAccessServiceIdSet.add(BUSINESS_ADDRESS);
        }

        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_EMAILS)) {
            fragmentAccessServiceIdSet.add(VERIFIED_EMAIL);
        }

        if (hasServicesAccessibility(ServiceIdConstants.SEE_BUSINESS_DOCS)) {
            fragmentAccessServiceIdSet.add(BUSINESS_DOCUMENTS);
        }
        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS)) {
            fragmentAccessServiceIdSet.add(VERIFICATION_DOCUMENT);
        }
        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_IDENTIFICATION_DOCS)) {
            fragmentAccessServiceIdSet.add(PHOTOID);
        }

        if (hasServicesAccessibility(ServiceIdConstants.SEE_PROFILE_COMPLETION)) {
            fragmentAccessServiceIdSet.add(PROFILE_COMPLETENESS);
        }

        if (hasServicesAccessibility(ServiceIdConstants.MANAGE_PROFILE_PICTURE)) {
            fragmentAccessServiceIdSet.add(PROFILE_PICTURE);
        }

        preferences.edit().putStringSet(Constants.FRAGMENT_SERVICE_ACCESS_SET, fragmentAccessServiceIdSet).apply();
    }
}
