package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney;

public class SendMoneyResponse {

    private String message;
    private long otpValidFor;

    public SendMoneyResponse() {

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
