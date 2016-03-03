package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class SignupRequestPersonal {

    private String mobileNumber;
    private String deviceId;
    private String firstName;
    private String lastName;
    private String dob;
    private String passwordHash;
    private String gender;
    private String otp;
    private String promoCode;
    private int accountType;

    public SignupRequestPersonal(String mobileNumber, String deviceId, String firstName,
                                 String lastName, String dob, String passwordHash, String gender,
                                 String otp, String promoCode, int accountType) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.otp = otp;
        this.promoCode = promoCode;
        this.accountType = accountType;
    }
}
