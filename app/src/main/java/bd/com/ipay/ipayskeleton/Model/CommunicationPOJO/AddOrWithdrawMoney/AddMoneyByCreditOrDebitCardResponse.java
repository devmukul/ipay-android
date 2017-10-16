package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class AddMoneyByCreditOrDebitCardResponse {

    private String message;
    private String forwardUrl;

    public AddMoneyByCreditOrDebitCardResponse() {

    }

    public String getMessage() {
        return message;
    }

    public String getForwardUrl() {
        return forwardUrl;
    }
}
