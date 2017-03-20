package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comment {
    private long comment_id;
    private String body;
    private String authorId;
    private Long createdAt;
    private List<String> documents;

    public Comment(String body, String authorId) {
        this.body = body;
        this.authorId = authorId;
    }

    public String getBody() {
        return body;
    }

    public String getAuthorId() {
        return authorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) { this.documents = documents; }

    public long getComment_Id() { return comment_id; }

    @Override
    public String toString() {
        return "Comment{" +
                "body='" + body + '\'' +
                ", authorId='" + authorId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
