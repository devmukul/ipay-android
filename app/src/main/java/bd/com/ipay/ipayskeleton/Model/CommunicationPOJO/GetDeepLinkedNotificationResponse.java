package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;


import java.util.List;

public class GetDeepLinkedNotificationResponse {
    private List<DeepLinkedNotification> notificationList;
    private String message;

    public List<DeepLinkedNotification> getNotificationList() {
        return notificationList;
    }

    public String getMessage() {
        return message;
    }
}
