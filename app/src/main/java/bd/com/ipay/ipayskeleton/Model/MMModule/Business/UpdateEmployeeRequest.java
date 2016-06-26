package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

import java.util.List;

public class UpdateEmployeeRequest {
    private String designation;
    private int id;
    private List<Privilege> privilegeList;

    public UpdateEmployeeRequest(String designation, int id, List<Privilege> privilegeList) {
        this.designation = designation;
        this.id = id;
        this.privilegeList = privilegeList;
    }
}
