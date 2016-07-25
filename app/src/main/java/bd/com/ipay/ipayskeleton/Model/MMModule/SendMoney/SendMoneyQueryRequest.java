package bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney;

class SendMoneyQueryRequest {

    private final String sender;
    private final String receiver;
    private final double amount;

    public SendMoneyQueryRequest(String sender, String receiver, String amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = Double.parseDouble(amount);
    }
}

