package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class BanglalionBillPayResponse {

    private String transactionId;
    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public BanglalionBillPayResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
