package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class AddressClass {
    public String addressLine1;
    public String addressLine2;
    public String city;
    public String district;
    public String postalCode;
    public String country;

    public AddressClass(String addressLine1, String addressLine2, String city,
                   String district, String postalCode, String country) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.district = district;
        this.postalCode = postalCode;
        this.country = country;
    }
}