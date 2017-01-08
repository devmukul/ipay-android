package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

public class GetTicketsResponse {

    private int statusCode;
    private String message;
    private TicketResponse response;
    private String requesterId;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public TicketResponse getResponse() {
        return response;
    }

    public String getRequesterId() {
        return requesterId;
    }
}
