package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TopUp;

public class TopupResponse {

    private String transactionID;
    private int statusCode;
    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {

        return otpValidFor;
    }

    public void setOtpValidFor(long otpValidFor) {
        this.otpValidFor = otpValidFor;
    }

    public TopupResponse() {
    }

    public String getTransactionID() {
        return transactionID;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
