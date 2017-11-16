package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;


public class BusinessRoleAcceptOrRejectRequest {
    private long id;
    private String status;

    public BusinessRoleAcceptOrRejectRequest(long id, String status) {
        this.id = id;
        this.status = status;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
