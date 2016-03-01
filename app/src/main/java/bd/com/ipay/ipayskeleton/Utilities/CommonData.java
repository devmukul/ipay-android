package bd.com.ipay.ipayskeleton.Utilities;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;

public class CommonData {
    private static List<Bank> availableBanks;

    public static List<Bank> getAvailableBanks() {
        return availableBanks;
    }

    public static boolean isAvailableBankListLoaded() {
        return availableBanks != null && !availableBanks.isEmpty();
    }

    // TODO: handle error (check for null etc)
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
}
