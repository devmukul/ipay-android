package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;


public class DeepLinkedNotification {
    private String accountId;
    private String deepLink;
    private String icon;
    private String body;
    private String status;
    private String message;
    private int serviceId;
    private long time;
    private String title;

    public DeepLinkedNotification(String accountId, String deepLink, String icon, String status, String message, int serviceId, int time, String title) {
        this.accountId = accountId;
        this.deepLink = deepLink;
        this.icon = icon;
        this.status = status;
        this.message = message;
        this.serviceId = serviceId;
        this.time = time;
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getDeepLink() {
        return deepLink;
    }

    public String getIcon() {
        return icon;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getServiceId() {
        return serviceId;
    }

    public long getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}

