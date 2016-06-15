package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;


public class OTPRequestTrustedDevice {

    private String mobileNumber;
    private String password;
    private String deviceId;
    private int accountType;
    private String promoCode;

    public OTPRequestTrustedDevice(String mobileNumber, String password, String deviceId, int accountType, String promoCode) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.deviceId = deviceId;
        this.accountType = accountType;
        this.promoCode = promoCode;
    }
}
