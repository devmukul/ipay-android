package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class AmberITBillPayRequest {
    private String customerId;
    private String amount;
    private String pin;
    private String otp;

    public AmberITBillPayRequest(String customerId, String amount, String pin) {
        this.customerId = customerId;
        this.amount = amount;
        this.pin = pin;
    }

    public AmberITBillPayRequest(String customerId, String amount, String pin, String otp) {

        this.customerId = customerId;
        this.amount = amount;
        this.pin = pin;
        this.otp = otp;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
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
}

