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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserLocation userLocation = (UserLocation) o;

        return Double.compare(userLocation.latitude, latitude) == 0 && Double.compare(userLocation.longitude, longitude) == 0 && createdAt == userLocation.createdAt;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (createdAt ^ (createdAt >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", createdAt=" + createdAt +
                '}';
    }
}
