package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;

public class SetProfileInfoRequest {

    private final String name;
    private final String gender;
    private final String dob;
    private final int occupation;
    private final String organizationName;

    public SetProfileInfoRequest(String name, String gender, String dob, int occupation, String organizationName) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.occupation = occupation;
        this.organizationName = organizationName;
    }
}