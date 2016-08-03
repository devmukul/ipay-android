package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class GetTicketsResponse {

    private int statusCode;
    private String message;
    private TicketResponse response;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public TicketResponse getResponse() {
        return response;
    }
}
