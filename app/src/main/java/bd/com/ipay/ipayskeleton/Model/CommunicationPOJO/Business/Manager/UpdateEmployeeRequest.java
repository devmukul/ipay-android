package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;


public class UpdateEmployeeRequest {

    private long managerAccountId;
    private long roleId;

    public UpdateEmployeeRequest(long managerAccountId, long roleId) {
        this.managerAccountId = managerAccountId;
        this.roleId = roleId;
    }

    public long getMobileNumber() {
        return managerAccountId;
    }

    public void setMobileNumber(long managerAccountId) {
        this.managerAccountId = managerAccountId;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }
}
