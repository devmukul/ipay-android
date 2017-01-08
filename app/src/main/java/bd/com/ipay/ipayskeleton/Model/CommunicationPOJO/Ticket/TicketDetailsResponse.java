package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class TicketDetailsResponse {
    private Ticket ticket;
    private Comments comments;

    public Ticket getTicket() {
        return ticket;
    }

    public Comments getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "TicketDetailsResponse{" +
                "ticket=" + ticket +
                ", comments=" + comments +
                '}';
    }
}
