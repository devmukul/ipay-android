package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.NotificationClass;

public class GetPendingRequestResponse {

    public List<RequestsSentClass> requests;
    public boolean hasNext;

    public GetPendingRequestResponse() {
    }

    public List<RequestsSentClass> getAllNotifications() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
