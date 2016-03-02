package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

import java.util.List;

public class GetDistrictResponse {

    private String message;
    private List<District> resource;

    public GetDistrictResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<District> getDistricts() {
        return resource;
    }
}
