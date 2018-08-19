package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


public class DpdcBillPayRequest {
    private String accountNumber;
    private String pin;
    private String otp;
    private String locationCode;

    public DpdcBillPayRequest(String accountNumber, String pin, String otp, String locationCode) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.otp = otp;
        this.locationCode = locationCode;
    }


    public String getAccountNumber() {

        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public DpdcBillPayRequest(String accountNumber, String pin, String locationCode) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.locationCode = locationCode;
    }

    public DpdcBillPayRequest(String accountNumber, String pin) {
        this.accountNumber = accountNumber;
        this.pin = pin;
    }
}
