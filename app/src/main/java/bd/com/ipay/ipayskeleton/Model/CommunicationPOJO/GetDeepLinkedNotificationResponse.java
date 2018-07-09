package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;


import java.util.List;

public class GetDeepLinkedNotificationResponse {
    private List<DeepLinkedNotification> notificationList;
    private String message;
    private boolean hasNext;
    private int unseenCount;

    public int getUnseenCount() {
        return unseenCount;
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
