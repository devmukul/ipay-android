package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo;

public class SetProfileInfoRequest {

    public String name;
    public String gender;
    public String dob;
    public int occupation;
    public String father;
    public String mother;

    public SetProfileInfoRequest(String name, String gender, String dob, int occupation, String father, String mother) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.occupation = occupation;
        this.father = father;
        this.mother = mother;
    }
}