package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class SignupRequestBusiness {

    public String mobileNumber;
    public String deviceId;
    public String name;
    public int accountType;
    public String dob;
    public String passwordHash;
    public String gender;
    public String otp;
    public String businessName;
    public String businessType;
    public String businessEmail;
    public String personalEmail;
    public String personalMobileNumber;
    public AddressClass personalAddress;
    public AddressClass businessAddress;
    private String promoCode;

    public SignupRequestBusiness(String mobileNumber, String deviceId,
                                 String name, int accountType, String dob,
                                 String passwordHash, String gender, String otp, String businessName,
                                 String businessType, String businessEmail, String personalEmail,
                                 String personalMobileNumber, AddressClass personalAddress,
                                 AddressClass businessAddress, String promoCode) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.name = name;
        this.accountType = accountType;
        this.dob = dob;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.otp = otp;
        this.businessName = businessName;
        this.businessType = businessType;
        this.businessEmail = businessEmail;
        this.personalEmail = personalEmail;
        this.personalMobileNumber = personalMobileNumber;
        this.personalAddress = personalAddress;
        this.businessAddress = businessAddress;
        this.promoCode = promoCode;
    }
}
