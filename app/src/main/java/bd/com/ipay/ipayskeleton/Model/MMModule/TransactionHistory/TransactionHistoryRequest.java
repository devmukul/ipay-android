package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class TransactionHistoryRequest {

    private final Integer serviceID;
    private final int page;
    private final Long fromDate;
    private final Long toDate;
    private final Integer limit;

    public TransactionHistoryRequest(Integer serviceID, int page, Long fromDate, Long toDate, Integer limit) {
        this.serviceID = serviceID;
        this.page = page;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.limit = limit;
    }

    public TransactionHistoryRequest(Integer serviceID, int page) {
        this(serviceID, page, null, null, null);
    }
}
