package bd.com.ipay.ipayskeleton.Model.MMModule.Ticket;

public class Requester {
    private String name;
    private String email;

    public Requester(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
