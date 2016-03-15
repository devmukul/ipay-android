package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class SignupRequestPersonal {

    private String mobileNumber;
    private String deviceId;
    private String name;
    private String dob;
    private String password;
    private String gender;
    private String otp;
    private String promoCode;
    private int accountType;

    public SignupRequestPersonal(String mobileNumber, String deviceId, String name,
                                 String dob, String password, String gender,
                                 String otp, String promoCode, int accountType) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.name = name;
        this.dob = dob;
        this.password = password;
        this.gender = gender;
        this.otp = otp;
        this.promoCode = promoCode;
        this.accountType = accountType;
    }
}
