package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class AddBankRequest {

    private long bankId;
    private int accountType;
    private String accountName;
    private String accountNumber;

    public AddBankRequest(long bankId, int accountType, String accountName, String accountNumber) {
        this.bankId = bankId;
        this.accountType = accountType;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }
}
