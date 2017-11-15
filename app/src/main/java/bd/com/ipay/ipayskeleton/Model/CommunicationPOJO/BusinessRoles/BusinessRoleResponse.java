package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

public class BusinessRoleResponse {

    private List<BusinessRole> businessRoleList;
    private String message;

    public List<BusinessRole> getBusinessRoleList() {
        return businessRoleList;
    }

    public String getMessage() {
        return message;
    }
}
