package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

import java.util.List;

public class UserActivityResponse {

    public List<UserActivityClass> activities;
    public boolean hasNext;

    public UserActivityResponse() {
    }

    public List<UserActivityClass> getActivities() {
        return activities;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
