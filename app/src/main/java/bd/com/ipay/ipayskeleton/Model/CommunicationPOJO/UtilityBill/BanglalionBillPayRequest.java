package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class BanglalionBillPayRequest {

    private final String customerId;
    private final int amount;
    private final String pin;
    private String otp;

    public BanglalionBillPayRequest(String customerId, int amount, String pin) {
        this.customerId = customerId;
        this.amount = amount;
        this.pin = pin;
    }

    public  String getOtp() {
        return otp;
    }

    public  void setOtp(String otp) {
        this.otp = otp;
    }

}
