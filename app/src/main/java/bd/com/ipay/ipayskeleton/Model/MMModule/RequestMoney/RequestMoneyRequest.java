package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

public class RequestMoneyRequest {

    public String receiver;
    public double amount;
    public String title;
    public String description;
    public String pin;

    public RequestMoneyRequest(String receiver, double amount, String title, String description, String pin) {
        this.receiver = receiver;
        this.amount = amount;
        this.title = title;
        this.description = description;
        this.pin = pin;
    }
}
