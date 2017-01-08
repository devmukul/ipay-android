package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

public class PaymentAcceptRejectOrCancelRequest {

    private final long requestId;
    private String pin;

    public PaymentAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }

    public PaymentAcceptRejectOrCancelRequest(long requestId) {
        this.requestId = requestId;
    }
}
