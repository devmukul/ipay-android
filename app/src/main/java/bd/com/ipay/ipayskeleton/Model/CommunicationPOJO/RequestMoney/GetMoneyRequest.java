package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class GetMoneyRequest {

    private final int page;
    private final int serviceID;
    private Integer status;


    public GetMoneyRequest(int page, int serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }

    public GetMoneyRequest(int page, int serviceID, Integer status) {
        this.page = page;
        this.serviceID = serviceID;
        this.status = status;
    }
}
