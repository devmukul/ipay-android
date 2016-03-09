package bd.com.ipay.ipayskeleton.Model.MMModule.Profile;

public class SetProfileInfoRequest {

    public String mobileNumber;
    public String name;
    public String gender;
    public String dob;
    public String country;
    public String email;
    public String NIDNumber;
    public String occupation;
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String district;
    public String postalCode;

    public SetProfileInfoRequest(String mobileNumber, String name, String gender,
                                 String dob, String country, String email, String NIDNumber, String occupation,
                                 String addressLine1, String addressLine2, String city, String district, String postalCode) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.country = country;
        this.email = email;
        this.NIDNumber = NIDNumber;
        this.occupation = occupation;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
    }
}


