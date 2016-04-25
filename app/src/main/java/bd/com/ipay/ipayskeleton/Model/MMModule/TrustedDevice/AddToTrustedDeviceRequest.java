package bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice;

import android.os.Build;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddToTrustedDeviceRequest {

    private String deviceName;
    private String deviceId;
    private boolean isMobileBrowser;
    private String osName;

    public AddToTrustedDeviceRequest(String deviceName, String deviceId) {
        this.deviceName = deviceName;
        this.deviceId = deviceId;
        isMobileBrowser = false;
        osName = Constants.ANDROID + " " + Build.VERSION.RELEASE;
    }
}
