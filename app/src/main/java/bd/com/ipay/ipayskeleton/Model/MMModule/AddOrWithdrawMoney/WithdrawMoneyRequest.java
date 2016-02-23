package bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney;

public class WithdrawMoneyRequest {

    public long bankAccountId;
    private double amount;
    private String description;

    public WithdrawMoneyRequest(long bankAccountId, double amount, String description) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.description = description;
    }
}
