package bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite;

public class RecommendationActionRequest {

    private long verificationRequestId;
    private String status;

    public RecommendationActionRequest(long verificationRequestId, String status) {
        this.verificationRequestId = verificationRequestId;
        this.status = status;
    }
}
