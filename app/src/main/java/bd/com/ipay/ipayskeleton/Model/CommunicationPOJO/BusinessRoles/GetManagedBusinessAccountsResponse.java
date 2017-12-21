package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

public class GetManagedBusinessAccountsResponse {
    private String message;
    private List<BusinessAccountDetails> businessList;

    public String getMessage() {
        return message;
    }

    public List<BusinessAccountDetails> getBusinessList() {
        return businessList;
    }
}
