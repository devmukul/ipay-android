package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class WithdrawMoneyResponse {

    private String message;
    private long otpValidFor;

    public WithdrawMoneyResponse() {

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
