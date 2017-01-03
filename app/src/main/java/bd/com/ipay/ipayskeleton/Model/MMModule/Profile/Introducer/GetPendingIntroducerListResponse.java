package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

import java.util.List;

public class GetPendingIntroducerListResponse {
    private String message;
    private List<PendingIntroducer> wantToBeIntroducers;

    public GetPendingIntroducerListResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<PendingIntroducer> getWantToBeIntroducers() {
        return wantToBeIntroducers;
    }
}
