package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UserActivity;

import java.util.List;

public class UserActivityResponse {

    private List<UserActivityClass> activities;
    private boolean hasNext;

    public UserActivityResponse() {
    }

    public List<UserActivityClass> getActivities() {
        return activities;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
