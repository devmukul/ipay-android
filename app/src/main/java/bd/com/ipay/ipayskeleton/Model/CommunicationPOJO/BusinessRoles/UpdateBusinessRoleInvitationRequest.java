package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles;

public class UpdateBusinessRoleInvitationRequest {
    private Long id;
    private String status;

    public UpdateBusinessRoleInvitationRequest(Long id, String status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}

