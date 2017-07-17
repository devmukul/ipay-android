package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import java.util.List;

public class GetThanaResponse {

    private String message;
    private List<Thana> resource;

    public GetThanaResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<Thana> getThanas() {
        return resource;
    }
}
