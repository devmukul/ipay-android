package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork;

public class AddTrustedPersonRequest {
    private String name;
    private String mobileNumber;
    private String relationship;

    public AddTrustedPersonRequest(String name, String mobileNumber, String relationship) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.relationship = relationship;
    }
}
