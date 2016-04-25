package bd.com.ipay.ipayskeleton.Utilities.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.PinInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;

public class CommonData {
    private static List<Bank> availableBanks;
    private static Map<Long, Bank> availableBanksMap;

    private static List<BusinessType> businessTypes;
    private static Map<Long, BusinessType> businessIdToTypeMap;
    private static Map<String, Long> businessNameToIdMap;

    public static List<Bank> getAvailableBanks() {
        return availableBanks;
    }

    public static boolean isAvailableBankListLoaded() {
        return availableBanks != null && !availableBanks.isEmpty();
    }

    public static String[] getAvailableBankNames() {
        String[] availableBankNames = new String[availableBanks.size()];
        for (int i = 0; i < availableBanks.size(); i++) {
            availableBankNames[i] = availableBanks.get(i).getName();
        }

        return availableBankNames;
    }

    public static Bank getBankById(long id) {
        return availableBanksMap.get(id);
    }

    public static void setAvailableBanks(List<Bank> banks) {
        CommonData.availableBanks = banks;
        availableBanksMap = new HashMap<>();
        for (Bank bank : banks) {
            availableBanksMap.put(bank.getId(), bank);
        }
    }



    public static List<BusinessType> getBusinessTypes() {
        return businessTypes;
    }

    public static boolean isBusinessTypesLoaded() {
        return businessTypes != null && !businessTypes.isEmpty();
    }

    public static String[] getBusinessTypeNames() {
        String[] businessTypes = new String[CommonData.businessTypes.size()];
        for (int i = 0; i < CommonData.businessTypes.size(); i++) {
            businessTypes[i] = CommonData.businessTypes.get(i).getName();
        }

        return businessTypes;
    }

    public static Long getBusinessTypeId(String businessName) {
        return businessNameToIdMap.get(businessName);
    }

    public static BusinessType getBusinessTypeById(long id) {
        return businessIdToTypeMap.get(id);
    }

    public static void setBusinessTypes(List<BusinessType> businessTypes) {
        CommonData.businessTypes = businessTypes;
        businessIdToTypeMap = new HashMap<>();
        businessNameToIdMap = new HashMap<>();
        for (BusinessType businessType: businessTypes) {
            businessIdToTypeMap.put(businessType.getId(), businessType);
            businessNameToIdMap.put(businessType.getName(), businessType.getId());
        }
    }
}
