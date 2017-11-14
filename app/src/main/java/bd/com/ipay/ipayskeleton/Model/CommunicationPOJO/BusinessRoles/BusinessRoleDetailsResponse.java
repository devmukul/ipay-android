package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;


import java.util.List;

public class BusinessRoleDetailsResponse {

    private String message;
    private long id;
    private String roleName;
    private long businessAccountId;
    private List<BusinessService> mBusinessServiceList;

    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }

    public long getBusinessAccountId() {
        return businessAccountId;
    }

    public List<BusinessService> getmBusinessServiceList() {
        return mBusinessServiceList;
    }
}
