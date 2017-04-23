package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import java.util.List;

public class GetRelationshipResponse {
    private String message;
    private List<Relationship> resource;

    public GetRelationshipResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<Relationship> getRelationships() {
        return resource;
    }
}

