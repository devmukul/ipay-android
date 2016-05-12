package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

public class GetNotificationsRequest {

    private int page;
    private Integer serviceID = null;

    public GetNotificationsRequest(int page) {
        this.page = page;
    }

    public GetNotificationsRequest(int page, int serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }
}
