package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource;

import java.util.ArrayList;
import java.util.List;

public class GetOccupationResponse {

    private String message;
    private List<Occupation> resource;

    public GetOccupationResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<Occupation> getOccupations() {
        return resource;
    }

    public List<String> getOccupationNames() {
        List<String> occupationNames = new ArrayList<>();
        for (Occupation occupation : resource) {
            occupationNames.add(occupation.getName());
        }

        return occupationNames;
    }

    public String getOccupation(long id) {
        for (Occupation occupation : getOccupations()) {
            if (occupation.getId() == id)
                return occupation.getName();
        }

        return null;
    }
}
