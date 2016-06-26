package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

import java.util.List;

public class GetBusinessListResponse {
    private List<Business> businessList;
    private String message;

    public List<Business> getBusinessList() {
        return businessList;
    }

    public String getMessage() {
        return message;
    }
}
