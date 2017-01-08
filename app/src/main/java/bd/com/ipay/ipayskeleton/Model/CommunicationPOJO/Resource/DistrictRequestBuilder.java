package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

public class DistrictRequestBuilder extends ResourceRequestBuilder {

    private static final String RESOURCE_TYPE_DISTRICT = "district";

    public DistrictRequestBuilder() {
        super();
    }

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE_DISTRICT;
    }
}
