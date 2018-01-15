package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

public class PaymentAcceptRejectOrCancelResponse {

    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public PaymentAcceptRejectOrCancelResponse() {

    }

    public String getMessage() {
        return message;
    }
}
