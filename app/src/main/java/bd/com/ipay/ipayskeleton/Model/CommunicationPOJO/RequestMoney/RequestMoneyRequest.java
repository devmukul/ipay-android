package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class RequestMoneyRequest {

    private final String receiver;
    private final double amount;
    private final String description;
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public RequestMoneyRequest(String receiver, double amount, String description) {
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
    }
}
