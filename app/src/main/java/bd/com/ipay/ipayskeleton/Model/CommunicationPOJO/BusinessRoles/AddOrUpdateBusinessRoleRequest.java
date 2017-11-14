package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;


import java.util.List;

public class AddOrUpdateBusinessRoleRequest {

    private String roleName;
    private List<BusinessService> mBusinessServiceList;

    public AddOrUpdateBusinessRoleRequest(String roleName, List<BusinessService> mBusinessServiceList) {
        this.roleName = roleName;
        this.mBusinessServiceList = mBusinessServiceList;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<BusinessService> getmBusinessServiceList() {
        return mBusinessServiceList;
    }

    public void setmBusinessServiceList(List<BusinessService> mBusinessServiceList) {
        this.mBusinessServiceList = mBusinessServiceList;
    }
}
