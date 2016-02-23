package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class TransactionHistoryRequest {

    private Integer type;
    private int page;

    public TransactionHistoryRequest(Integer type, int page) {
        this.type = type;
        this.page = page;
    }
}
