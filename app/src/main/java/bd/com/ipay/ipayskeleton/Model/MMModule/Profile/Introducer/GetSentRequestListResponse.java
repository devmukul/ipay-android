package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

import java.util.List;


public class GetSentRequestListResponse {
    private String message;
    private List<SentRequest> verificationRequests;

    public GetSentRequestListResponse () {}

    public String getMessage() {
        return message;
    }

    public List<SentRequest> getSentRequestList() { return verificationRequests; }
}
