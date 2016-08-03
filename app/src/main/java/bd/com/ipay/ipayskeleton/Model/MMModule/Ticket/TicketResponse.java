package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

import java.util.List;

public class TicketResponse {
    private List<Ticket> tickets;
    private String nextPage;
    private String previousPage;
    private int count;

    public List<Ticket> getTickets() {
        return tickets;
    }

    public String getNextPage() {
        return nextPage;
    }

    public String getPreviousPage() {
        return previousPage;
    }

    public int getCount() {
        return count;
    }
}
