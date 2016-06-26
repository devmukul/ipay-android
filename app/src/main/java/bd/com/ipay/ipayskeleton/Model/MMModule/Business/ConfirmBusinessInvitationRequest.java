package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

public class ConfirmBusinessInvitationRequest {
    private int associationId;
    private String status;

    public ConfirmBusinessInvitationRequest(int associationId, String status) {
        this.associationId = associationId;
        this.status = status;
    }
}
