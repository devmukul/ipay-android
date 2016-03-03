package bd.com.ipay.ipayskeleton.Model.MMModule.Invite;

import java.util.List;

public class SendInviteRequest {
    public List<String> invitees;

    public SendInviteRequest(List<String> invitees) {
        this.invitees = invitees;
    }
}
