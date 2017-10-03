package bd.com.ipay.ipayskeleton.DataCollectors.Model;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class LocationCollector {

    @NonNull
    private String mobileNumber;
    @NonNull
    private String deviceId;
    @NonNull
    private String uuid;
    @NonNull
    private List<UserLocation> locationList;

    public LocationCollector() {
        this("", "", "", Collections.<UserLocation>emptyList());
    }

    @SuppressWarnings("WeakerAccess")
    public LocationCollector(@NonNull String mobileNumber, @NonNull String deviceId, @NonNull String uuid, @NonNull List<UserLocation> locationList) {

        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.uuid = uuid;
        this.locationList = locationList;
    }

    @NonNull
    public String getMobileNumber() {

        return mobileNumber;
    }

    public void setMobileNumber(@NonNull String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    @NonNull
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(@NonNull String deviceId) {
        this.deviceId = deviceId;
    }

    @NonNull
    public String getUuid() {
        return uuid;
    }

    public void setUuid(@NonNull String uuid) {
        this.uuid = uuid;
    }

    @NonNull
    public List<UserLocation> getLocationList() {
        return locationList;
    }

    public void setLocationList(@NonNull List<UserLocation> locationList) {
        this.locationList = locationList;
    }


    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationCollector that = (LocationCollector) o;

        if (!mobileNumber.equals(that.mobileNumber)) return false;
        if (!deviceId.equals(that.deviceId)) return false;
        if (!uuid.equals(that.uuid)) return false;
        return locationList.equals(that.locationList);
    }

    @Override
    public int hashCode() {
        int result = mobileNumber.hashCode();
        result = 31 * result + deviceId.hashCode();
        result = 31 * result + uuid.hashCode();
        result = 31 * result + locationList.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LocationCollector{" +
                "mobileNumber='" + mobileNumber + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", uuid='" + uuid + '\'' +
                ", locationList=" + locationList +
                '}';
    }
}
