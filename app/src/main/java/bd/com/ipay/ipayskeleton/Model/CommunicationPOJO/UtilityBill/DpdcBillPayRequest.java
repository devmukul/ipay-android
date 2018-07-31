package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;



public class DpdcBillPayRequest {
    private String accountNumber;
    private String pin;
    private String otp;

    public DpdcBillPayRequest(String accountNumber, String pin, String otp) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.otp = otp;
    }

    public DpdcBillPayRequest(String accountNumber, String pin) {
        this.accountNumber = accountNumber;
        this.pin = pin;
    }
}
