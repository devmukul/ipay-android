package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

public class ForgetPasswordRequest {

    private String mobileNumber;
    private String deviceId;

    public ForgetPasswordRequest(String mobileNumber, String deviceId) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
    }
}
