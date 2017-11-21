package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


public class PaymentRequest {

    private final String mobileNumber;
    private final double amount;
    private final String description;
    private final String pin;
    private final String ref;
    private String otp;

    public PaymentRequest(String mobileNumber, String amount, String description, String pin, String ref) {
        this.mobileNumber = mobileNumber;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.pin = pin;
        this.ref = ref;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
