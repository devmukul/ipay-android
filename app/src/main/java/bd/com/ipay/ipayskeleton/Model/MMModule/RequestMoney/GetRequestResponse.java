package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.util.List;

public class GetRequestResponse {

    private List<RequestsSentClass> requests;
    private boolean hasNext;

    public GetRequestResponse() {
    }

    public List<RequestsSentClass> getAllNotifications() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
