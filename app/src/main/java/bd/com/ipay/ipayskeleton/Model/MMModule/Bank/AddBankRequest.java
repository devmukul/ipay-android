package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class AddBankRequest {

    private long bankBranchId;
    private int accountType;
    private String accountName;
    private String accountNumber;

    public AddBankRequest(long bankBranchId, int accountType, String accountName, String accountNumber) {
        this.bankBranchId = bankBranchId;
        this.accountType = accountType;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }
}
