package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class TransactionHistoryRequest {

    private Integer type;
    private int page;
    private String fromDate;
    private String toDate;

    public TransactionHistoryRequest(Integer type, int page, String fromDate, String toDate) {
        this.type = type;
        this.page = page;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public TransactionHistoryRequest(Integer type, int page) {
        this(type, page, null, null);
    }
}
