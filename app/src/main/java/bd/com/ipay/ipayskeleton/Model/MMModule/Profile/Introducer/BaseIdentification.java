package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;


public abstract class BaseIdentification {

    private String name;
    private String mobileNumber;
    private String profilePictureUrl;

    public abstract String getName();

    public abstract String getMobileNumber();

    public abstract String getProfilePictureUrl();
}
