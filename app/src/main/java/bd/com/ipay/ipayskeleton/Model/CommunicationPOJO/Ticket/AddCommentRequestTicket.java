package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class AddCommentRequestTicket {
    private long id;
    private String requesterId;
    private String submitterId;
    private String status;
    private String subject;
    private Comment comment;

    public AddCommentRequestTicket(long id, String requesterId, String submitterId, String status, String subject, Comment comment) {
        this.id = id;
        this.requesterId = requesterId;
        this.submitterId = submitterId;
        this.status = status;
        this.subject = subject;
        this.comment = comment;
    }
}
