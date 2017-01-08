package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class AddCommentRequest {
    private long ticketId;
    private String comment;

    public AddCommentRequest(long ticketId, String comment) {
        this.ticketId = ticketId;
        this.comment = comment;
    }
}
