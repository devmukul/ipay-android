package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;


public class CreateEmployeeRequest {

    private String mobileNumber;
    private long roleId;

    public CreateEmployeeRequest(String mobileNumber, long roleId) {
        this.mobileNumber = mobileNumber;
        this.roleId = roleId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }
}
