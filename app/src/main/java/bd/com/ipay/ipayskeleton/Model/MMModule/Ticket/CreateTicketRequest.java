package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class CreateTicketRequest {
    private String subject;
    private String message;

    public CreateTicketRequest(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }
}
