package bd.com.ipay.ipayskeleton.Utilities;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;

public class CommonData {
    private static List<Bank> availableBanks;
    private static Map<Long, Bank> availableBanksMap;

    private static List<Thana> thanas;
    private static List<District> districts;

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


    public static List<Thana> getThanas() {
        return thanas;
    }

    public static boolean isThanasLoaded() {
        return thanas != null && !thanas.isEmpty();
    }

    public static String[] getThanaNames() {
        String[] thanaNames = new String[thanas.size()];
        for (int i = 0; i < thanas.size(); i++) {
            thanaNames[i] = thanas.get(i).getName();
        }

        return thanaNames;
    }

    public static void setThanas(List<Thana> thanas) {
        CommonData.thanas = thanas;
    }

    public static String[] getThanaNamesWithNoSelection() {
        String[] thanaNames = getThanaNames();
        String[] thanaNamesWithNoSelection = new String[thanaNames.length + 1];
        thanaNamesWithNoSelection[0] = "No Selection";
        System.arraycopy(thanaNames, 0, thanaNamesWithNoSelection, 1, thanaNames.length);
        return thanaNamesWithNoSelection;
    }




    public static List<District> getDistricts() {
        return districts;
    }

    public static boolean isDistrictLoaded() {
        return districts != null && !districts.isEmpty();
    }

    public static String[] getDistrictNames() {
        String[] districtNames = new String[districts.size()];
        for (int i = 0; i < districts.size(); i++) {
            districtNames[i] = districts.get(i).getName();
        }

        return districtNames;
    }

    public static String[] getDistrictNamesWithNoSelection() {
        String[] districtNames = getDistrictNames();
        String[] districtNamesWithNoSelection = new String[districtNames.length + 1];
        districtNamesWithNoSelection[0] = "No Selection";
        System.arraycopy(districtNames, 0, districtNamesWithNoSelection, 1, districtNames.length);
        return districtNamesWithNoSelection;
    }

    public static void setDistricts(List<District> districts) {
        CommonData.districts = districts;
    }
}
