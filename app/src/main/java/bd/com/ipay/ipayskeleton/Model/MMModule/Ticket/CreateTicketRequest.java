package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class CreateTicketRequest {
    private Requester ticketCreator;
    private String subject;

    public CreateTicketRequest(Requester ticketCreator, String subject) {
        this.ticketCreator = ticketCreator;
        this.subject = subject;
    }

    public Requester getTicketCreator() {
        return ticketCreator;
    }

    public String getSubject() {
        return subject;
    }
}
