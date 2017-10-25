package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney;

public class RequestMoneyRequest {

    private final String receiver;
    private final double amount;
    private final String description;
    private String otp;
    private String pin;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public RequestMoneyRequest(String receiver, double amount, String description, String pin) {
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
        this.pin = pin;
    }

    public RequestMoneyRequest(String receiver, double amount, String description, String otp, String pin) {

        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
        this.otp = otp;
        this.pin = pin;
    }

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
