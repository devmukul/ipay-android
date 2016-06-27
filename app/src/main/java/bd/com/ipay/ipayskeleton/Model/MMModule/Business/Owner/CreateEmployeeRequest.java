package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner;

import java.util.List;

public class CreateEmployeeRequest {
    private String designation;
    private String mobileNumber;
    private List<Privilege> privilegeList;

    public CreateEmployeeRequest(String mobileNumber, String designation, List<Privilege> privilegeList) {
        this.designation = designation;
        this.mobileNumber = mobileNumber;
        this.privilegeList = privilegeList;
    }
}
