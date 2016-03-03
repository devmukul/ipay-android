package bd.com.ipay.ipayskeleton.Model.MMModule.Invite;

import java.util.List;

public class GetInviteInfoRequest {
    public int totalLimit;
    public List<String> invitees;

    public GetInviteInfoRequest(int totalLimit, List<String> invitees) {
        this.totalLimit = totalLimit;
        this.invitees = invitees;
    }
}
