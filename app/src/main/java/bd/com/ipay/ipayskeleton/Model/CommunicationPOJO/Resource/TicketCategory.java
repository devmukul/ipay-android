package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

public class TicketCategory implements Resource {
    private String code;
    private String name;

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStringId() {
        return code;
    }


}
