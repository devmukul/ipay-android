package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class LoginRequest {

    private String mobileNumber;
    private String passwordHash;
    private String deviceId;
    private String uuid;
    private String otp;
    private String captcha;

    public LoginRequest(String mobileNumber, String passwordHash, String deviceId, String uuid, String otp, String captcha) {
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.otp = otp;
        this.captcha = captcha;
    }
}
