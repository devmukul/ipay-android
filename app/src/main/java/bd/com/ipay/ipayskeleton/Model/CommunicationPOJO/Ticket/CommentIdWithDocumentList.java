package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import java.util.List;

public class CommentIdWithDocumentList {
    private long comment_id;
    private long id;
    private List<String> documents;

    public long getComment_id() {
        return comment_id;
    }

    public long getId() {
        return id;
    }

    public List<String> getDocuments() {
        return documents;
    }
}
