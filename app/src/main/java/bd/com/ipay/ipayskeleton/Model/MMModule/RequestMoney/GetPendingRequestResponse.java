package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.util.List;

public class GetPendingRequestResponse {

    private List<RequestsSentClass> requests;
    private boolean hasNext;

    public GetPendingRequestResponse() {
    }

    public List<RequestsSentClass> getAllNotifications() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
