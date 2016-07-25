package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

public class SetProfileInfoRequest {

    private final String name;
    private final String gender;
    private final String dob;
    private final int occupation;
    private final String father;
    private final String mother;

    public SetProfileInfoRequest(String name, String gender, String dob, int occupation, String father, String mother) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.occupation = occupation;
        this.father = father;
        this.mother = mother;
    }
}