package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite;

import java.util.List;

public class GetIntroductionRequestsResponse {

    private String message;
    private List<IntroductionRequestClass> verificationRequestList;

    public GetIntroductionRequestsResponse() {
    }

    public String getMessage() {
        return message;
    }

    public List<IntroductionRequestClass> getVerificationRequestList() {
        return verificationRequestList;
    }
}
