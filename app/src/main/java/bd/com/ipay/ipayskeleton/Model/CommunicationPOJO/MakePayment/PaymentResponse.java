package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


public class PaymentResponse {

    private String message;
    private long otpValidFor;
    private String transactionId;

    public PaymentResponse() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
