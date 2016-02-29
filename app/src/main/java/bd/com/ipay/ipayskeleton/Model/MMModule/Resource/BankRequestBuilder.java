package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class BankRequestBuilder extends ResourceRequestBuilder {

    private static final String RESOURCE_TYPE_BANK = "bank";

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE_BANK;
    }
}
