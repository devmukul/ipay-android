package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


public class BrilliantBillPayRequest {
    private String brilliantNumber;
    private long amount;
    private String pin;
    private String otp;

    public BrilliantBillPayRequest(String brilliantNumber, long amount, String pin) {
        this.brilliantNumber = brilliantNumber;
        this.amount = amount;
        this.pin = pin;
    }

    public BrilliantBillPayRequest(String brilliantNumber, long amount, String pin, String otp) {
        this.brilliantNumber = brilliantNumber;
        this.amount = amount;
        this.pin = pin;
        this.otp = otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getBrilliantNumber() {
        return brilliantNumber;
    }

    public long getAmount() {
        return amount;
    }

    public String getPin() {
        return pin;
    }

    public String getOtp() {
        return otp;
    }
}

