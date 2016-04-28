package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;

public class AddBankRequest {

    private String branchRoutingNumber;
    private int accountType;
    private String accountName;
    private String accountNumber;

    public AddBankRequest(String branchRoutingNumber, int accountType, String accountName, String accountNumber) {
        this.branchRoutingNumber = branchRoutingNumber;
        this.accountType = accountType;
        this.accountName = accountName;
        this.accountNumber = accountNumber;
    }
}
