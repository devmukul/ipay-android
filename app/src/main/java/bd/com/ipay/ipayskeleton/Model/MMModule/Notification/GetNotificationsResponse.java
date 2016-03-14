package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

import java.util.List;

public class GetNotificationsResponse {

    public List<NotificationClass> requests;
    public boolean hasNext;

    public GetNotificationsResponse() {
    }

    public List<NotificationClass> getAllNotifications() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
