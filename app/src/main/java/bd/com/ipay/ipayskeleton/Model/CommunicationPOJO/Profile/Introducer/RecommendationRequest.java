package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Introducer;

public class RecommendationRequest {
    private String name;
    private String mobileNumber;
    private String profilePictureUrl;
    private String status;

    public RecommendationRequest() {
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
