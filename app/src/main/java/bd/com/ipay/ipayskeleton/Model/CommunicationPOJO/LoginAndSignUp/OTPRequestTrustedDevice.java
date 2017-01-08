package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;


public class OTPRequestTrustedDevice {

    private final String mobileNumber;
    private final String password;
    private final String deviceId;
    private final int accountType;

    public OTPRequestTrustedDevice(String mobileNumber, String password, String deviceId, int accountType) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.deviceId = deviceId;
        this.accountType = accountType;
    }
}
