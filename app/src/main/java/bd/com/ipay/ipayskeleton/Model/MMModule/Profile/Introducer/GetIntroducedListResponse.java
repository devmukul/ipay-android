package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer;


import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.Introducer;

public class GetIntroducedListResponse {
    private String message;
    private List<Introduced> introducedList;

    public GetIntroducedListResponse() {}

    public String getMessage() {
            return message;
        }

    public List<Introduced> getIntroducedList() { return introducedList; }


}
