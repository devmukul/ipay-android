package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class AddMoneyResponse {

    private String message;
    private long otpValidFor;

    public AddMoneyResponse() {

    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
