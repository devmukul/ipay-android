package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import com.google.gson.Gson;

public class FCMNotificationResponse {
    private int serviceId;
    private boolean isReceiver;
    private String changed_data;

    public int getServiceId() {
        return serviceId;
    }

    public boolean isReceiver() {
        return isReceiver;
    }

    public FCMNotificationData getNotificationData() {
        FCMNotificationData fcmNotificationData;
        Gson gson = new Gson();
        fcmNotificationData = gson.fromJson(changed_data, FCMNotificationData.class);
        return fcmNotificationData;
    }
}
