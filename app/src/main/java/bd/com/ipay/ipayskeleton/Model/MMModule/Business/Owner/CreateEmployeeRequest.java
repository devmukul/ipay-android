package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner;

public class CreateEmployeeRequest {
    private String designation;
    private String mobileNumber;
    private int roleId;

    public CreateEmployeeRequest(String mobileNumber, String designation, int roleId) {
        this.designation = designation;
        this.mobileNumber = mobileNumber;
        this.roleId = roleId;
    }
}
