package bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney;

public class SendMoneyRequest {

    private String sender;
    private String receiver;
    private double amount;
    private String description;
    private String pin;

    public SendMoneyRequest(String sender, String receiver, String amount, String description, String pin) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.pin = pin;
    }
}
