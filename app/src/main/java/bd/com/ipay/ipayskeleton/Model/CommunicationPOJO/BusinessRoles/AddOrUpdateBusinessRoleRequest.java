package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;


import java.util.List;

public class AddOrUpdateBusinessRoleRequest {

    private String roleName;
    private List<BusinessService> serviceList;

    public AddOrUpdateBusinessRoleRequest(String roleName, List<BusinessService> serviceList) {
        this.roleName = roleName;
        this.serviceList = serviceList;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<BusinessService> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<BusinessService> serviceList) {
        this.serviceList = serviceList;
    }
}
