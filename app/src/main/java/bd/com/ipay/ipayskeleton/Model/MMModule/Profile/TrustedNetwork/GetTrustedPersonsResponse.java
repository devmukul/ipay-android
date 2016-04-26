package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork;

import java.util.List;

public class GetTrustedPersonsResponse {
    private String message;
    private List<TrustedPerson> trustedPersons;

    public String getMessage() {
        return message;
    }

    public List<TrustedPerson> getTrustedPersons() {
        return trustedPersons;
    }
}
