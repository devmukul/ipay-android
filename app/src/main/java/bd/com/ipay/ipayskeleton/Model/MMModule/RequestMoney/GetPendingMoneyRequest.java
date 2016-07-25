package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class GetPendingMoneyRequest {

    private final int page;
    private final int serviceID;

    public GetPendingMoneyRequest(int page, int serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }
}
