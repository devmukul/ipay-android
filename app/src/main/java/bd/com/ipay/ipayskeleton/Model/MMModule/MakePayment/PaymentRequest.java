package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;


public class PaymentRequest {

    private String mobileNumber;
    private double amount;
    private String description;
    private String pin;

    public PaymentRequest(String mobileNumber, String amount, String description, String pin) {
        this.mobileNumber = mobileNumber;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.pin = pin;
    }
}
