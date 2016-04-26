package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.TrustedNetwork;

public class AddTrustedPersonRequest {
    private long personId;
    private String name;
    private String relationShip;

    public AddTrustedPersonRequest(long personId, String name, String relationShip) {
        this.personId = personId;
        this.name = name;
        this.relationShip = relationShip;
    }
}
