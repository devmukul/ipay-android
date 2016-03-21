package bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrustedDevice {
    public long id;
    public String deviceName;
    public String deviceId;
    public String browserName;
    public boolean isMobileBrowser;
    public String screenSize;
    public String osName;
    public long createdTime;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, H:MM a");
        Date date = new Date();
        date.setTime(createdTime);
        return dateFormat.format(date);
    }

}
