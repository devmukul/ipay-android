package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;


public class CreateEmployeeRequest {

    private String mobileNumber;
    private long roleID;

    public CreateEmployeeRequest(String mobileNumber, long roleID) {
        this.mobileNumber = mobileNumber;
        this.roleID = roleID;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public long getRoleID() {
        return roleID;
    }

    public void setRoleID(long roleID) {
        this.roleID = roleID;
    }
}
