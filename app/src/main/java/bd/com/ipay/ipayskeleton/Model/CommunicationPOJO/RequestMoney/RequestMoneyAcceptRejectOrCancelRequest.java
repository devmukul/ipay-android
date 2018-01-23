package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class RequestMoneyAcceptRejectOrCancelRequest {

    private long requestId;
    private String transactionId;
    private String pin;
    private String otp;

    public RequestMoneyAcceptRejectOrCancelRequest(long requestId, String pin) {
        this.requestId = requestId;
        this.pin = pin;
    }

    public RequestMoneyAcceptRejectOrCancelRequest(String transactionId, String pin) {
        this.transactionId = transactionId;
        this.pin = pin;
    }


    public RequestMoneyAcceptRejectOrCancelRequest(long requestId) {
        this.requestId = requestId;
    }

    public RequestMoneyAcceptRejectOrCancelRequest(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
