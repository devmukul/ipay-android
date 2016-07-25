package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;


public class OTPRequestTrustedDevice {

    private final String mobileNumber;
    private final String password;
    private final String deviceId;
    private final int accountType;
    private final String promoCode;

    public OTPRequestTrustedDevice(String mobileNumber, String password, String deviceId, int accountType, String promoCode) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.deviceId = deviceId;
        this.accountType = accountType;
        this.promoCode = promoCode;
    }
}
