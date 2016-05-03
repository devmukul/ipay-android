package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class BankBranch {
    private String routingNumber;
    private String name;

    public BankBranch() {

    }

    public BankBranch(String branchRoutingNumber, String name) {
        this.routingNumber = branchRoutingNumber;
        this.name = name;
    }

    public String getRoutingNumber() {
        return routingNumber;
    }

    public String getName() {
        return name;
    }
}
