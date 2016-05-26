package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class RequestMoneyRequest {

    public String receiver;
    public double amount;
    public String title;
    public String description;

    public RequestMoneyRequest(String receiver, double amount, String title, String description) {
        this.receiver = receiver;
        this.amount = amount;
        this.title = title;
        this.description = description;
    }
}
