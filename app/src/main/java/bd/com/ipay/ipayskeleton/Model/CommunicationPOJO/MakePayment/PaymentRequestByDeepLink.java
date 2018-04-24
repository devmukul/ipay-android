package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


public class PaymentRequestByDeepLink {

    private String pin;
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public PaymentRequestByDeepLink(String pin) {
        this.pin = pin;
    }

    public PaymentRequestByDeepLink(String pin, String otp) {
        this.pin = pin;
        this.otp = otp;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
