package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class OTPRequestPersonalSignup {

    private String mobileNumber;
    private String deviceId;
    private int accountType;

    public OTPRequestPersonalSignup(String mobileNumber, String deviceId, int accountType) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.accountType = accountType;
    }
}
