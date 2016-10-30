package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

public class SetProfileInfoRequest {

    private final String name;
    private final String gender;
    private final long dob;
    private final int occupation;

    public SetProfileInfoRequest(String name, String gender, long dob, int occupation) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.occupation = occupation;
    }
}