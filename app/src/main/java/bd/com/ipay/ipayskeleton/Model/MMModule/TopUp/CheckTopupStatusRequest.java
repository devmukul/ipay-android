package bd.com.ipay.ipayskeleton.Model.MMModule.TopUp;

public class CheckTopupStatusRequest {

    private String mobileNumber;
    private String passwordHash;
    private String deviceId;

    public CheckTopupStatusRequest(String mobileNumber, String passwordHash, String deviceId) {
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
        this.deviceId = deviceId;
    }
}
