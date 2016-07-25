package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class OTPRequestPersonalSignup {

    private final String mobileNumber;
    private final String deviceId;
    private final int accountType;
    private final String promoCode;

    public OTPRequestPersonalSignup(String mobileNumber, String deviceId, int accountType, String promoCode) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.accountType = accountType;
        this.promoCode = promoCode;
    }

}
