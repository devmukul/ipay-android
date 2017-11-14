package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class RequestMoneyAcceptRejectOrCancelRequest {

    private final long requestId;
    private String pin;
    private String otp;

    public RequestMoneyAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public RequestMoneyAcceptRejectOrCancelRequest(long requestId) {
        this.requestId = requestId;
    }
}
