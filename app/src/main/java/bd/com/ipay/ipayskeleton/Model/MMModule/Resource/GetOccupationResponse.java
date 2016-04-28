package bd.com.ipay.ipayskeleton.Model.MMModule.Resource;

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

    public String getOccupation(long id) {
        for (Occupation occupation : getOccupations()) {
            if (occupation.getId() == id)
                return occupation.getName();
        }

        return null;
    }
}
