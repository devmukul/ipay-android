package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ForgetPassword;

public class ForgetPasswordRequest {

    private final String name;
    private final String mobileNumber;
    private final String dob;
    private final String deviceId;

    public ForgetPasswordRequest(String name, String mobileNumber, String dob, String deviceId) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.dob = dob;
        this.deviceId = deviceId;
    }
}
