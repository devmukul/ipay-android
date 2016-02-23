package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class GetPendingRequest {

    public String mobileNumber;
    public int page;

    public GetPendingRequest(String mobileNumber, int page) {
        this.mobileNumber = mobileNumber;
        this.page = page;
    }
}
