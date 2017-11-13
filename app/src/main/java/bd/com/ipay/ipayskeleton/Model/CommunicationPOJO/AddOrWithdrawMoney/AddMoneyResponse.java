package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class AddMoneyResponse {

    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public AddMoneyResponse() {

    }

    public String getMessage() {
        return message;
    }
}
