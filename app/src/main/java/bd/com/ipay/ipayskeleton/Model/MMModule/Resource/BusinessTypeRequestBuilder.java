package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

public class BusinessTypeRequestBuilder extends ResourceRequestBuilder {

    private static final String RESOURCE_TYPE_BUSINESS = "businessType";

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE_BUSINESS;
    }
}
