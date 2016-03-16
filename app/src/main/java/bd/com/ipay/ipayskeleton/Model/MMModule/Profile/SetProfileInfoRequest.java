package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class SetProfileInfoRequest {

    public String name;
    public String gender;
    public String dob;
    public String email;
    public String occupation;
    public String father;
    public String mother;
    public String spouse;


    public SetProfileInfoRequest(String name, String gender, String dob, String email, String occupation, String father, String mother, String spouse) {
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.occupation = occupation;
        this.father = father;
        this.mother = mother;
        this.spouse = spouse;
    }
}


