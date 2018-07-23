package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class Link3BillPayResponse {

    private String transactionId;
    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public Link3BillPayResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
