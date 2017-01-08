package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.TrustedNetwork;

public class TrustedPerson {
    private long personId;
    private String name;
    private String relationship;
    private String mobileNumber;
    private boolean eligibleForAccountRecovery;

    public long getPersonId() {
        return personId;
    }

    public String getName() {
        return name;
    }

    public String getRelationship() {
        return relationship;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public boolean isEligibleForAccountRecovery() {
        return eligibleForAccountRecovery;
    }
}
