package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee;

public class ConfirmBusinessInvitationRequest {
    private final long associationId;
    private final String status;

    public ConfirmBusinessInvitationRequest(long associationId, String status) {
        this.associationId = associationId;
        this.status = status;
    }
}
