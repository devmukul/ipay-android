package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

public class UserActivityClass {

    public long id;
    public long accountId;
    public int type;
    public String description;
    public long time;

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
