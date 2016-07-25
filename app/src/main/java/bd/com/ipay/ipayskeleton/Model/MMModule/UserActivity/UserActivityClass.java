package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

public class UserActivityClass {

    private long id;
    private long accountId;
    private int type;
    private String description;
    private long time;

    public UserActivityClass() {
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
