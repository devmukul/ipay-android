package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

import java.util.List;


public class GetRecommendationRequestsResponse {
    private String message;
    private List<RecommendationRequest> verificationRequests;

    public GetRecommendationRequestsResponse() {}

    public String getMessage() {
        return message;
    }

    public List<RecommendationRequest> getSentRequestList() { return verificationRequests; }
}
