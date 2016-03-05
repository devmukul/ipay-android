package bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite;

import java.util.List;

public class SendInviteRequest {
    public List<String> invitees;

    public SendInviteRequest(List<String> invitees) {
        this.invitees = invitees;
    }
}
