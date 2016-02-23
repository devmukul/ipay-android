package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class OTPRequestBusinessSignup {

    private String mobileNumber;
    private String deviceId;
    private int accountType;

    public OTPRequestBusinessSignup(String mobileNumber, String deviceId, int accountType) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.accountType = accountType;
    }
}
