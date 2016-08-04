package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class AddCommentRequest {
    AddCommentRequestTicket ticket;

    private AddCommentRequest(AddCommentRequestTicket ticket) {
        this.ticket = ticket;
    }

    public static AddCommentRequest createFromTicket(Ticket ticket, String message) {
        AddCommentRequestTicket addCommentRequest = new AddCommentRequestTicket(ticket.getId(), ticket.getRequesterId(),
                ticket.getRequesterId(), ticket.getStatus(), ticket.getSubject(), new Comment(message, ticket.getRequesterId()));
        return new AddCommentRequest(addCommentRequest);
    }
}
