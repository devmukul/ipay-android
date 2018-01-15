package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

public class PaymentAcceptRejectOrCancelRequest {

    private long requestId;
    private String transactionId;
    private String pin;
    private String otp;

    public PaymentAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }

    public PaymentAcceptRejectOrCancelRequest(String transactionId, String pin) {
        this.transactionId = transactionId;
        this.pin = pin;
    }


    public PaymentAcceptRejectOrCancelRequest(long requestId) {
        this.requestId = requestId;
    }

    public PaymentAcceptRejectOrCancelRequest(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
