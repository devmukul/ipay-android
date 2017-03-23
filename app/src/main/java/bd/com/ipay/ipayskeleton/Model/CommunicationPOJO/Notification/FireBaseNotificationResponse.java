package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

public class FireBaseNotificationResponse {
    private FireBaseNotification changed_data;
    private String icon;
    private int serviceId;

    public FireBaseNotification getChanged_data() {
        return changed_data;
    }

    public String getIcon() {
        return icon;
    }

    public int getServiceId() {
        return serviceId;
    }
}
