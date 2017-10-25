package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


public class PaymentResponse {

    private String message;
    private long otpValidFor;

    public PaymentResponse() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public void setOtpValidFor(long otpValidFor) {
        this.otpValidFor = otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
