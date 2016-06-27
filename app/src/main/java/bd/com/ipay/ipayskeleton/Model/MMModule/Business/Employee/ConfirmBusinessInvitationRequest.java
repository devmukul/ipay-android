package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

public class ConfirmBusinessInvitationRequest {
    private long associationId;
    private String status;

    public ConfirmBusinessInvitationRequest(long associationId, String status) {
        this.associationId = associationId;
        this.status = status;
    }
}
