package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


public class DescoBillPayResponse {
    private String message;
    private String transactionId;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}