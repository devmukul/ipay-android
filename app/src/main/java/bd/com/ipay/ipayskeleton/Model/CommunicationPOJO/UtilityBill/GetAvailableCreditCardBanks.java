package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard.Bank;

public class GetAvailableCreditCardBanks {
    private String message;
    private ArrayList<Bank> bankList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Bank> getBankList() {
        return bankList;
    }

    public void setBankList(ArrayList<Bank> bankList) {
        this.bankList = bankList;
    }
}
