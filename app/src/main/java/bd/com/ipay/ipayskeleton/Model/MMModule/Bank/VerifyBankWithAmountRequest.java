package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class VerifyBankWithAmountRequest {

    private Long userBankID;
    private Double amount;

    public VerifyBankWithAmountRequest(Long userBankID, double amount) {
        this.userBankID = userBankID;
        this.amount = amount;
    }
}
