package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class AddCommentRequest {
    private long id;
    private String requesterId;
    private String submitterId;
    private String status;
    private Comment comment;

    private AddCommentRequest(long id, String requesterId, String submitterId, String status, Comment comment) {
        this.id = id;
        this.requesterId = requesterId;
        this.submitterId = submitterId;
        this.status = status;
        this.comment = comment;
    }

    public static AddCommentRequest createFromTicket(Ticket ticket, String message) {
        AddCommentRequest addCommentRequest = new AddCommentRequest(ticket.getId(), ticket.getRequesterId(),
                ticket.getRequesterId(), ticket.getStatus(), new Comment(message, ticket.getRequesterId()));
        return addCommentRequest;
    }
}
