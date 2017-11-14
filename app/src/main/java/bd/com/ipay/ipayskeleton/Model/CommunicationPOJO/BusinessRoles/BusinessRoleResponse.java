package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

import java.util.List;

public class BusinessRoleResponse {

    private List<BusinessRole> mBusinessRoleList;
    private String message;

    public List<BusinessRole> getmBusinessRoleList() {
        return mBusinessRoleList;
    }

    public String getMessage() {
        return message;
    }
}
