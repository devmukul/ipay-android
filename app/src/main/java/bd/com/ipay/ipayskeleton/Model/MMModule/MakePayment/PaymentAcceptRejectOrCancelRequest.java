package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

public class PaymentAcceptRejectOrCancelRequest {

    private long requestId;
    private String pin;

    public PaymentAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }
}
