package bd.com.ipay.ipayskeleton.Model.MMModule.TopUp;

public class TopupResponse {

    private String transactionID;
    private int statusID;
    private String statusDescription;

    public TopupResponse() {
    }

    public String getTransactionID() {
        return transactionID;
    }

    public int getStatusID() {
        return statusID;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}
