package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;

public class Introducer {
    private String name;
    private String mobileNumber;
    private String profilePictureUrl;
    private Long introducedDate;

    public Introducer() {

    }

    public String getprofilePictureUrl() { return profilePictureUrl ; }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public Long getIntroducedDate() {
        return introducedDate;
    }
}
