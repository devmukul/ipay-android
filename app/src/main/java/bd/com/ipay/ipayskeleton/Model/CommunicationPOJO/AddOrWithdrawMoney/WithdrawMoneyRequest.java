package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class WithdrawMoneyRequest {

    private final long bankAccountId;
    private final double amount;
    private final String description;
    private final String pin;
    private String otp;

    public WithdrawMoneyRequest(long bankAccountId, double amount, String description, String pin) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.description = description;
        this.pin = pin;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
