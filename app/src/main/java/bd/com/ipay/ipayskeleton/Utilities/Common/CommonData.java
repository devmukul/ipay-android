package bd.com.ipay.ipayskeleton.Utilities.Common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Relationship;

public class CommonData {
    private static List<Bank> availableBanks;
    private static Map<Integer, Bank> availableBanksMap;

    private static List<BusinessType> businessTypes;
    private static Map<Integer, BusinessType> businessIdToTypeMap;
    private static Map<String, Integer> businessNameToIdMap;
    private static List<Relationship> relationshipList;

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

    public static Bank getBankById(Integer id) {
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

    public static Integer getBusinessTypeId(String businessName) {
        return businessNameToIdMap.get(businessName);
    }

    public static BusinessType getBusinessTypeById(Integer id) {
        return businessIdToTypeMap.get(id);
    }

    public static List<Relationship> getRelationshipList() {
        return CommonData.relationshipList;
    }

    public static void setBusinessTypes(List<BusinessType> businessTypes) {
        CommonData.businessTypes = businessTypes;
        businessIdToTypeMap = new HashMap<>();
        businessNameToIdMap = new HashMap<>();
        for (BusinessType businessType : businessTypes) {
            businessIdToTypeMap.put(businessType.getId(), businessType);
            businessNameToIdMap.put(businessType.getName(), businessType.getId());
        }
    }

    public static void setRelationshipList(List<Relationship> relationshipList) {
        CommonData.relationshipList = relationshipList;
    }
}
