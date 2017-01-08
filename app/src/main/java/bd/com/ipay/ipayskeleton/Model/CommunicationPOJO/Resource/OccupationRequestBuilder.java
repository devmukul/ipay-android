package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

public class OccupationRequestBuilder extends ResourceRequestBuilder {

    private static final String RESOURCE_TYPE_OCCUPATION = "occupation";

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE_OCCUPATION;
    }
}
