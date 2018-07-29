package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


public class WestZoneBillPayRequest {
    private String accountNumber;
    private String otp;
    private String pin;

    public WestZoneBillPayRequest(String accountNumber, String otp, String pin) {
        this.accountNumber = accountNumber;
        this.otp = otp;
        this.pin = pin;
    }

    public WestZoneBillPayRequest(String accountNumber, String pin) {
        this.accountNumber = accountNumber;
        this.pin = pin;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getOtp() {
        return otp;
    }

    public String getPin() {
        return pin;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
