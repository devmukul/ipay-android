package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo;

public class SetProfileInfoRequest {

    private String name;
    private String gender;
    private String dob;
    private int occupation;
    private String organizationName;

    public SetProfileInfoRequest(String name, String gender, String dob, int occupation, String organizationName) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.occupation = occupation;
        this.organizationName = organizationName;
    }

    public SetProfileInfoRequest(int occupation, String organizationName) {
        this.occupation = occupation;
        this.organizationName = organizationName;
    }
}