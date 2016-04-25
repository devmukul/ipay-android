package bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice;

public class AddToTrustedDeviceRequest {

    private String deviceName;
    private String deviceId;
    private boolean isMobileBrowser;
    private String osName;

    public AddToTrustedDeviceRequest(String deviceName, String deviceId, String osName) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        isMobileBrowser = false;
        this.osName = osName;
    }
}
