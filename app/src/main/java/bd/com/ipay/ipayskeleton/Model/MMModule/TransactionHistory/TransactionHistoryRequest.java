package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class TransactionHistoryRequest {

    private Integer serviceID;
    private int page;
    private Long fromDate;
    private Long toDate;

    public TransactionHistoryRequest(Integer serviceID, int page, Long fromDate, Long toDate) {
        this.serviceID = serviceID;
        this.page = page;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public TransactionHistoryRequest(Integer serviceID, int page) {
        this(serviceID, page, null, null);
    }
}
