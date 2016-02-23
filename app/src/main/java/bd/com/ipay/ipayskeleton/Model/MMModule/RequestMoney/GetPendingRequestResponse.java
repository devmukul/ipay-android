package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.util.List;

public class GetPendingRequestResponse {

    private List<PendingMoneyRequestClass> requests;
    private boolean hasNext;

    public GetPendingRequestResponse() {

    }

    public List<PendingMoneyRequestClass> getRequests() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
