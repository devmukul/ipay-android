package bd.com.ipay.ipayskeleton.DataCollectors.Model;

@SuppressWarnings("unused")
public class UserLocation {

    private transient int id = -1;
    private double latitude;
    private double longitude;
    private long createdAt;

    public UserLocation() {
        this(0, 0);
    }

    public UserLocation(double latitude, double longitude) {
        this(latitude, longitude, System.currentTimeMillis());
    }

    @SuppressWarnings("WeakerAccess")
    public UserLocation(double latitude, double longitude, long createdAt) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
