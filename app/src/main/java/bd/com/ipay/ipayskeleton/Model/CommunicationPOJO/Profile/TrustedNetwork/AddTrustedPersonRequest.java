package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.TrustedNetwork;

public class AddTrustedPersonRequest {
    private final String name;
    private final String mobileNumber;
    private final String relationship;

    public AddTrustedPersonRequest(String name, String mobileNumber, String relationship) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.relationship = relationship;
    }
}
