package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class RequestMoneyRequest {

    private final String receiver;
    private final double amount;
    private final String title;
    private final String description;

    public RequestMoneyRequest(String receiver, double amount, String title, String description) {
        this.receiver = receiver;
        this.amount = amount;
        this.title = title;
        this.description = description;
    }
}
