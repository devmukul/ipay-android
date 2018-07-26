package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;



public class BrilliantRechargeResponse {
    private String message;
    private String transactionId;
    private long otpValidFor;

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
