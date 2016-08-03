package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class Comment {
    private String body;
    private String authorId;
    private long createdAt;

    public String getBody() {
        return body;
    }

    public String getAuthorId() {
        return authorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "body='" + body + '\'' +
                ", authorId='" + authorId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
