package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.TicketCategory;

public class GetTicketCategoriesResponse {
    private int statusCode;
    private String message;
    private List<TicketCategory> response;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public List<TicketCategory> getTicketCategories() {
        return response;
    }
}
