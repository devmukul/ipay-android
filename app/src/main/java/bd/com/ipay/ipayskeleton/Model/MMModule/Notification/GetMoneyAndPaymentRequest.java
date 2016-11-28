package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

public class GetMoneyAndPaymentRequest {

    private Integer page;
    private Integer serviceID;
    private Integer status;

    public GetMoneyAndPaymentRequest() {
    }

    public GetMoneyAndPaymentRequest(int page) {
        this.page = page;
    }

    public GetMoneyAndPaymentRequest(Integer page, Integer serviceID) {
        this.page = page;
        this.serviceID = serviceID;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
