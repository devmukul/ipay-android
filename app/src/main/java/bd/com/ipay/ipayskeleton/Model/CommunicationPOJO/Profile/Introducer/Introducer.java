package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Introducer;

public class Introducer {
    private String name;
    private String mobileNumber;
    private String profilePictureUrl;
    private long introducedDate;

    public Introducer() {
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public long getIntroducedDate() {
        return introducedDate;
    }
}
