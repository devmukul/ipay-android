package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

public class PaymentRequestSentResponse {
    private String message;
    private  long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public PaymentRequestSentResponse() {

    }

    public String getMessage() {
        return message;
    }
}
