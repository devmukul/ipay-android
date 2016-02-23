package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.util.List;

public class GetPendingPaymentsResponse {

    private List<PendingPaymentClass> requests;
    private boolean hasNext;

    public GetPendingPaymentsResponse() {

    }

    public List<PendingPaymentClass> getRequests() {
        return requests;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
