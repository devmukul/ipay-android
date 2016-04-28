package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class BankBranch {
    private String branchRoutingNumber;
    private String name;

    public BankBranch() {

    }

    public BankBranch(String branchRoutingNumber, String name) {
        this.branchRoutingNumber = branchRoutingNumber;
        this.name = name;
    }

    public String getBranchRoutingNumber() {
        return branchRoutingNumber;
    }

    public String getName() {
        return name;
    }
}
