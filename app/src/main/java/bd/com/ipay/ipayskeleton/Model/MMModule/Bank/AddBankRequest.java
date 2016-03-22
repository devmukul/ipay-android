package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class AddBankRequest {

    private long bankBranch;
    private int accountType;
    private String accountName;
    private String accountNumber;

    public AddBankRequest(long bankBranch, int accountType, String accountName, String accountNumber) {
        this.bankBranch = bankBranch;
        this.accountType = accountType;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }
}
