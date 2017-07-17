package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp;

class CheckTopupStatusRequest {

    private final String mobileNumber;
    private final String passwordHash;
    private final String deviceId;

    public CheckTopupStatusRequest(String mobileNumber, String passwordHash, String deviceId) {
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
        this.deviceId = deviceId;
    }
}
