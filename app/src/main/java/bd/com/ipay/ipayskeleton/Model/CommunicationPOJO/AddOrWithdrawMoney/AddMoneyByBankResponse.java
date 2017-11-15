package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class AddMoneyByBankResponse {

    private String message;
    private long otpValidFor;

    public AddMoneyByBankResponse() {

    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
