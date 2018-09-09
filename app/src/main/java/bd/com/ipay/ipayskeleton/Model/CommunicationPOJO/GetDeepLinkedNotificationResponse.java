package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;


import java.util.List;

public class GetDeepLinkedNotificationResponse {
    private List<DeepLinkedNotification> notificationList;
    private String message;
    private boolean hasNext;
    private int notSeenCount;

    public int getNotSeenCount() {
        return notSeenCount;
    }

    public List<DeepLinkedNotification> getNotificationList() {
        return notificationList;
    }

    public String getMessage() {
        return message;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
