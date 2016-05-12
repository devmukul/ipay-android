package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.RecommendationAndInvite;

import java.util.List;

public class GetRecommendationRequestsResponse {

    private String message;
    private List<RecommendRequestClass> verificationRequestList;

    public GetRecommendationRequestsResponse() {
    }

    public String getMessage() {
        return message;
    }

    public List<RecommendRequestClass> getVerificationRequestList() {
        return verificationRequestList;
    }
}
