package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class GetPendingMoneyRequest {

    public int page;
    public int serviceID;

    public GetPendingMoneyRequest(int page, int serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }
}
