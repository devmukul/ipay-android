package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Introducer;


import java.util.List;

public class GetIntroducedListResponse {
    private String message;
    private List<Introduced> introducedList;

    public GetIntroducedListResponse() {}

    public String getMessage() {
            return message;
        }

    public List<Introduced> getIntroducedList() { return introducedList; }


}
