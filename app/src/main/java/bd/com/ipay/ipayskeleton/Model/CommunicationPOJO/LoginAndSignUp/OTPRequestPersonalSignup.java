package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class OTPRequestPersonalSignup {

    private final String mobileNumber;
    private final String deviceId;
    private final int accountType;

    public OTPRequestPersonalSignup(String mobileNumber, String deviceId, int accountType) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.accountType = accountType;
    }

}
