package bd.com.ipay.ipayskeleton.Model.MMModule.UserActivity;

class UserActivityRequest {

    private final Integer type;
    private final int page;
    private final String fromDate;
    private final String toDate;
    private final int count;

    public UserActivityRequest(Integer type, int page, String fromDate, String toDate, int count) {
        this.type = type;
        this.page = page;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.count = count;
    }
}
