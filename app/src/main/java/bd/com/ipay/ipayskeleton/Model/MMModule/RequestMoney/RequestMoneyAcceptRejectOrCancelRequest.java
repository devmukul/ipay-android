package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class RequestMoneyAcceptRejectOrCancelRequest {

    private long requestId;
    private String pin;

    public RequestMoneyAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }
}
