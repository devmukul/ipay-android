package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney;

class SendMoneyQueryResponse {

    private String sender;
    private String receiver;
    private double amount;

    public SendMoneyQueryResponse() {
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }
}
