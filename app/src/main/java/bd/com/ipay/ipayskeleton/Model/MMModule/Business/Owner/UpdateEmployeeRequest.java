package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner;

import java.util.List;

public class UpdateEmployeeRequest {
    private final String designation;
    private final long id;
    private final List<Privilege> privilegeList;

    public UpdateEmployeeRequest(String designation, long id, List<Privilege> privilegeList) {
        this.designation = designation;
        this.id = id;
        this.privilegeList = privilegeList;
    }
}
