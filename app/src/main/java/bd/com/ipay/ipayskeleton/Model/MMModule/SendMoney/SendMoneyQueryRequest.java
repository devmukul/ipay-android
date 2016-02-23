package bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney;

public class SendMoneyQueryRequest {

    private String sender;
    private String receiver;
    private double amount;

    public SendMoneyQueryRequest(String sender, String receiver, String amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = Double.parseDouble(amount);
    }
}

