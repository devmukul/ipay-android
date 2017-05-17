package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RefreshToken;

public class FCMRefreshTokenRequest {
    private final String firebaseToken;
    private final String deviceId;
    private final String deviceType;

    public FCMRefreshTokenRequest(String firebaseToken, String deviceId, String deviceType) {
        this.firebaseToken = firebaseToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }
}
