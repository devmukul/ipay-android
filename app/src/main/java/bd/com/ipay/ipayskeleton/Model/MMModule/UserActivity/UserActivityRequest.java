package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

public class UserActivityRequest {

    private Integer type;
    private int page;
    private String fromDate;
    private String toDate;
    private int count;

    public UserActivityRequest(Integer type, int page, String fromDate, String toDate, int count) {
        this.type = type;
        this.page = page;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.count = count;
    }
}
