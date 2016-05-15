package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

import java.util.List;

public class GetIntroducerListResponse {
    private String message;
    private List<Introducer> introducers;
    private int requiredForProfileCompletion;

    public GetIntroducerListResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<Introducer> getIntroducers() {
        return introducers;
    }

    public int getRequiredForProfileCompletion() {
        return requiredForProfileCompletion;
    }
}
