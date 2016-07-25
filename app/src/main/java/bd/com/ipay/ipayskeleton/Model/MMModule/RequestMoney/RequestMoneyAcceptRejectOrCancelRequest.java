package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class RequestMoneyAcceptRejectOrCancelRequest {

    private final long requestId;
    private String pin;

    public RequestMoneyAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }

    public RequestMoneyAcceptRejectOrCancelRequest(long requestId) {
        this.requestId = requestId;
    }
}
