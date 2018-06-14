package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;


public class PaymentRevertRequest {

    private final String purpose;
    private final String transactionId;
    private final String pin;

    public PaymentRevertRequest(String purpose, String transactionId, String pin) {
        this.purpose = purpose;
        this.transactionId = transactionId;
        this.pin = pin;
    }
}
