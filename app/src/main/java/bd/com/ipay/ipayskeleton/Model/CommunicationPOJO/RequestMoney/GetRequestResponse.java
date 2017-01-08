package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

import java.util.List;

public class GetRequestResponse {

    private List<MoneyRequest> requests;
    private boolean hasNext;

    public GetRequestResponse() {
    }

    public List<MoneyRequest> getAllNotifications() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
