package bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite;

import java.util.List;

public class GetInviteInfoResponse {
    public String message;
    public int totalLimit;
    public List<String> invitees;

    public GetInviteInfoResponse() {
    }

    public GetInviteInfoResponse(String message, int totalLimit, List<String> invitees) {
        this.message = message;
        this.totalLimit = totalLimit;
        this.invitees = invitees;
    }

    @Override
    public String toString() {
        return "GetInviteInfoResponse{" +
                "message='" + message + '\'' +
                ", totalLimit=" + totalLimit +
                ", invitees=" + invitees +
                '}';
    }
}
