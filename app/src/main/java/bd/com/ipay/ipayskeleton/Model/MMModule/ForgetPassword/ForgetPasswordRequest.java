package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

public class ForgetPasswordRequest {

    private String name;
    private String mobileNumber;
    private String dob;
    private String deviceId;

    public ForgetPasswordRequest(String name, String mobileNumber, String dob, String deviceId) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.dob = dob;
        this.deviceId = deviceId;
    }
}
