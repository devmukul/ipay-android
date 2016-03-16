package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

public class GetPendingPaymentsRequest {

    public int page;
    public int serviceID;

    public GetPendingPaymentsRequest(int page, int serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }
}
