package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

public class BankBranchRequestBuilder extends ResourceRequestBuilder {

    public BankBranchRequestBuilder(long filter) {
        super(filter);
    }

    private static final String RESOURCE_TYPE_BANK_BRANCH = "bankBranch";

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE_BANK_BRANCH;
    }
}
