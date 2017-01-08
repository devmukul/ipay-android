package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UserActivity;

public class UserActivityClass {

    private long id;
    private long accountId;
    private int type;
    private String description;
    private long time;
    private String ipAddress;
    private String location;

    public UserActivityClass() {
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getLocation() {
        return location;
    }

    public long getId() {
        return id;
    }

    public long getAccountId() {
        return accountId;
    }

    public int getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public long getTime() {
        return time;
    }
}
