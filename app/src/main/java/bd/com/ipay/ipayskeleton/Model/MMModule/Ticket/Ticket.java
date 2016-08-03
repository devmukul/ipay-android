package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class Ticket {
    private long id;
    private long requesterId;
    private String url;
    private long createdAt;
    private long updatedAt;
    private String subject;
    private String description;
    private String status;

    public long getId() {
        return id;
    }

    public long getRequesterId() {
        return requesterId;
    }

    public String getUrl() {
        return url;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }
}
