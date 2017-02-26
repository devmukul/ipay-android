package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner;

public class UpdateEmployeeRequest {
    private String designation;
    private long id;
    private int roleId;

    public UpdateEmployeeRequest(String designation, long id, int roleId) {
        this.designation = designation;
        this.id = id;
        this.roleId = roleId;
    }
}
