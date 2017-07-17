package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice;

import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TrustedDevice {
    private long id;
    private String deviceName;
    private String deviceId;
    private String browserName;
    private boolean isMobileBrowser;
    private String screenSize;
    private String osName;
    private long createdTime;

    public TrustedDevice() {

    }

    public long getId() {
        return id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getBrowserName() {
        return browserName;
    }

    public boolean isMobileBrowser() {
        return isMobileBrowser;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public String getOsName() {
        return osName;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public String getCreatedTimeString() {
        return Utilities.formatDateWithTime(createdTime);
    }

}
