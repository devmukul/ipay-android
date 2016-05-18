package bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice;

import android.os.Build;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddToTrustedDeviceRequest {

    private String deviceName;
    private String deviceId;
    private boolean isMobileBrowser;
    private String osName;
    private String pushRegistrationId;

    public AddToTrustedDeviceRequest(String deviceName, String deviceId, String pushRegistrationId) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        this.pushRegistrationId = pushRegistrationId;
        isMobileBrowser = false;
        osName = Constants.ANDROID + " " + Build.VERSION.RELEASE;
    }
}
