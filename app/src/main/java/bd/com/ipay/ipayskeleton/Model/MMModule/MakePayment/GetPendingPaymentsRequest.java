package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

public class GetPendingPaymentsRequest {

    private final int page;
    private final int serviceID;

    public GetPendingPaymentsRequest(int page, int serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }
}
