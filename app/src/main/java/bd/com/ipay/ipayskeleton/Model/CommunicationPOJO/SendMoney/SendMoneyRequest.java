package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney;

public class SendMoneyRequest {

    private final String sender;
    private final String receiver;
    private final double amount;
    private final String description;
    private String pin;
    private String otp;

    public SendMoneyRequest(String sender, String receiver, String amount, String pin, String description) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.otp = null;
        this.pin = pin;
    }

    public SendMoneyRequest(String sender, String receiver, String amount, String description, String pin, String otp) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = Double.parseDouble(amount);
        this.description = description;
        this.pin = pin;
        this.otp = otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
