package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class GetTicketDetailsResponse {
    private int statusCode;
    private String message;
    private TicketDetailsResponse response;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public TicketDetailsResponse getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "GetTicketDetailsResponse{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", response=" + response +
                '}';
    }
}
