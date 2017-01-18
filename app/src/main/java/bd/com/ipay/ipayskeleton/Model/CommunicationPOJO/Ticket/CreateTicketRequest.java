package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class CreateTicketRequest {
    private String subject;
    private String category;
    private String message;

    public CreateTicketRequest(String subject, String category, String message) {
        this.subject = subject;
        this.category = category;
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }
}
