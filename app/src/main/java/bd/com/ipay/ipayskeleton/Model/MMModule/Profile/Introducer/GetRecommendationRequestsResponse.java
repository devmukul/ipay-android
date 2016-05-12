package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

import java.util.List;


public class GetRecommendationRequestsResponse {
    private String message;
    private List<SentRequest> verificationRequests;

    public GetRecommendationRequestsResponse() {}

    public String getMessage() {
        return message;
    }

    public List<SentRequest> getSentRequestList() { return verificationRequests; }
}
