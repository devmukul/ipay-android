package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import java.util.List;

public class GetMoneyAndPaymentRequestResponse {

    private List<MoneyAndPaymentRequest> requests;
    private boolean hasNext;
    private String message;

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
