package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

public class GetPendingPaymentsRequest {

    public String mobileNumber;
    public int page;

    public GetPendingPaymentsRequest(String mobileNumber, int page) {
        this.mobileNumber = mobileNumber;
        this.page = page;
    }
}
