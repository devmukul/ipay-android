package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class LinkThreeBillPayRequest {
    private final String subscriberId;
    private final int amount;
    private final String pin;
    private String otp;

    public LinkThreeBillPayRequest(String subscriberId, int amount, String pin) {
        this.subscriberId = subscriberId;
        this.amount = amount;
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
