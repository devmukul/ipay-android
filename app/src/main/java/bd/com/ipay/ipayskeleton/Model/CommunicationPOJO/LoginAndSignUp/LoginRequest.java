package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class LoginRequest {

    private final String mobileNumber;
    private final String password;
    private final String deviceId;
    private final String uuid;
    private final String otp;
    private final String pushRegistrationId;
    private final String captcha;
    private final boolean isRemember;

    public LoginRequest(String mobileNumber, String password, String deviceId,
                        String uuid, String otp, String pushRegistrationId, String captcha, boolean isRemember) {
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.otp = otp;
        this.pushRegistrationId = pushRegistrationId;
        this.captcha = captcha;
        this.isRemember = isRemember;
    }
}
