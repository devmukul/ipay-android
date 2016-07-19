package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

import java.util.List;

public class GetMoneyAndPaymentRequestResponse {

    public List<MoneyAndPaymentRequest> requests;
    public boolean hasNext;
    public String message;

    public GetMoneyAndPaymentRequestResponse() {
    }

    public List<MoneyAndPaymentRequest> getAllMoneyAndPaymentRequests() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public String getMessage() {
        return message;
    }
}
