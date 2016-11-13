package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.util.List;

public class GetRequestResponse {

    private List<MoneyRequestClass> requests;
    private boolean hasNext;

    public GetRequestResponse() {
    }

    public List<MoneyRequestClass> getAllNotifications() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
