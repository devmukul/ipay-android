package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;


public class PaymentRequest {

    private final String mobileNumber;
    private final double amount;
    private final String description;
    private final String pin;

    public PaymentRequest(String mobileNumber, String amount, String description, String pin) {
        this.mobileNumber = mobileNumber;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.pin = pin;
    }
}
