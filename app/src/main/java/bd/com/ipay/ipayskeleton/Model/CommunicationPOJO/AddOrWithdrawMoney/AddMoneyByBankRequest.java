package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney;

public class AddMoneyByBankRequest {

    private final long bankAccountId;
    private final double amount;
    private final String description;
    private final String pin;
    private String otp;

    public AddMoneyByBankRequest(long bankAccountId, double amount, String description, String pin) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.description = description;
        this.pin = pin;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


}
