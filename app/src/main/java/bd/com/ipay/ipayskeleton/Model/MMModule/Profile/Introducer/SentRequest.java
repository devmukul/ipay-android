package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

public class SentRequest {
    private String name;
    private String mobileNumber;
    private String profilePictureUrl;
    private String status;

    public SentRequest() {
    }

    public String getName() {
        return name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getStatus() {
        return status;
    }
}
