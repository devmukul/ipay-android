package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class LoginRequest {

    private String mobileNumber;
    private String password;
    private String deviceId;
    private String uuid;
    private String otp;
    private String pushRegistrationId;
    private String captcha;

    public LoginRequest(String mobileNumber, String password, String deviceId,
                        String uuid, String otp, String pushRegistrationId, String captcha) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.otp = otp;
        this.pushRegistrationId = pushRegistrationId;
        this.captcha = captcha;
    }
}
