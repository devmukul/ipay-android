package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import java.util.List;

public class GetBusinessTypeResponse {

    private String message;
    private List<BusinessType> resource;

    public GetBusinessTypeResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<BusinessType> getBusinesses() {
        return resource;
    }
}
