package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class AddBankRequest {

    private final String branchRoutingNumber;
    private final int accountType;
    private final String accountName;
    private final String accountNumber;

    public AddBankRequest(String branchRoutingNumber, int accountType, String accountName, String accountNumber) {
        this.branchRoutingNumber = branchRoutingNumber;
        this.accountType = accountType;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }
}
