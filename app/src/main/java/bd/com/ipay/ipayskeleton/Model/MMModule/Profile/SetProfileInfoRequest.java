package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class SetProfileInfoRequest {

    public String mobileNumber;
    public String name;
    public String gender;
    public String dob;
    public String email;
    public int occupation;
    public String father;
    public String mother;
    public String spouse;
    public String fatherMobileNumber;
    public String motherMobileNumber;
    public String spouseMobileNumber;

    public SetProfileInfoRequest(String mobileNumber, String name, String gender, String dob,
                                 String email, int occupation, String father, String mother,
                                 String spouse, String fatherMobileNumber,
                                 String motherMobileNumber, String spouseMobileNumber) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.email = email;
        this.occupation = occupation;

        // Empty values not allowed
        if (father != null && !father.isEmpty())
            this.father = father;
        if (mother != null && !mother.isEmpty())
            this.mother = mother;
        if (spouse != null && !spouse.isEmpty())
            this.spouse = spouse;

        if (fatherMobileNumber != null && !fatherMobileNumber.isEmpty())
            this.fatherMobileNumber = fatherMobileNumber;
        if (motherMobileNumber != null && !motherMobileNumber.isEmpty())
            this.motherMobileNumber = motherMobileNumber;
        if (spouseMobileNumber != null && !spouseMobileNumber.isEmpty())
            this.spouseMobileNumber = spouseMobileNumber;
    }
}


