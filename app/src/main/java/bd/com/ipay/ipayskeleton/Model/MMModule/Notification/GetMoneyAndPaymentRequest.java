package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

public class GetMoneyAndPaymentRequest {

    private Integer page;
    private Integer serviceID;


    public GetMoneyAndPaymentRequest() {}

    public GetMoneyAndPaymentRequest(int page) {
        this.page = page;
    }

    public GetMoneyAndPaymentRequest(int page, int serviceID) {
        this.page = 0;
        this.serviceID = serviceID;
    }
}
