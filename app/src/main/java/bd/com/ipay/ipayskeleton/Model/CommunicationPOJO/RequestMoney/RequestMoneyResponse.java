package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class RequestMoneyResponse {

    private String message;
    private long otpValidFor;

    public void setMessage(String message) {
        this.message = message;
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public RequestMoneyResponse() {

    }

    public String getMessage() {
        return message;
    }
}
