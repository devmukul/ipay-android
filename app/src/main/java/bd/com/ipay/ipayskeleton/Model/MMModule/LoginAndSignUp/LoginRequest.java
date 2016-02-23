package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;

public class LoginRequest {

    private String mobileNumber;
    private String passwordHash;
    private String deviceId;

    public LoginRequest(String mobileNumber, String passwordHash, String deviceId) {
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
        this.deviceId = deviceId;
    }
}
