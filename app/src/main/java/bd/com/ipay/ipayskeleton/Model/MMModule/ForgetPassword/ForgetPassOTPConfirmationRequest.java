package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

public class ForgetPassOTPConfirmationRequest {

    private String mobileNumber;
    private String deviceId;
    private String otp;
    private String newPassword;

    public ForgetPassOTPConfirmationRequest(String mobileNumber, String deviceId, String otp, String newPassword) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.otp = otp;
        this.newPassword = newPassword;
    }
}
