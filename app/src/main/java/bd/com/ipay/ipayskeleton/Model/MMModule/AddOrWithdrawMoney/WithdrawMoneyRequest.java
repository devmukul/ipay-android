package bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney;

public class WithdrawMoneyRequest {

    public long bankAccountId;
    private double amount;
    private String description;
    private String pin;

    public WithdrawMoneyRequest(long bankAccountId, double amount, String description, String pin) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.description = description;
        this.pin = pin;
    }
}
