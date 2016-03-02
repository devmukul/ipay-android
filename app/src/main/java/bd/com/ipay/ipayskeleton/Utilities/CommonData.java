package bd.com.ipay.ipayskeleton.Utilities;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;

public class CommonData {
    private static List<Bank> availableBanks;
    private static List<Thana> thanas;

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

    public static void setAvailableBanks(List<Bank> banks) {
        CommonData.availableBanks = banks;
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
}
