package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class RequestMoneyAcceptRejectOrCancelResponse {

    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public RequestMoneyAcceptRejectOrCancelResponse() {

    }

    public String getMessage() {
        return message;
    }
}
