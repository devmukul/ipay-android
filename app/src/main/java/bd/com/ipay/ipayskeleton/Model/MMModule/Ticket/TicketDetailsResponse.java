package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

import java.util.List;

public class TicketDetailsResponse {
    private Ticket ticket;
    private List<Comment> comments;

    public Ticket getTicket() {
        return ticket;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
