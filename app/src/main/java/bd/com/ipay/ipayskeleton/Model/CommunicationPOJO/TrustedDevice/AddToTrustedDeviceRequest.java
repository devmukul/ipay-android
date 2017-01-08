package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice;

import android.os.Build;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddToTrustedDeviceRequest {

    private final String deviceName;
    private final String deviceId;
    private final boolean isMobileBrowser;
    private final String osName;
    private final String pushRegistrationId;

    public AddToTrustedDeviceRequest(String deviceName, String deviceId, String pushRegistrationId) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.pushRegistrationId = pushRegistrationId;
        isMobileBrowser = false;
        osName = Constants.ANDROID + " " + Build.VERSION.RELEASE;
    }
}
