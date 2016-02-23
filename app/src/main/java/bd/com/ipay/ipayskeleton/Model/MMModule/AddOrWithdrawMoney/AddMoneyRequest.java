package bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney;

public class AddMoneyRequest {

    public long bankAccountId;
    public double amount;
    public String description;


    public AddMoneyRequest(long bankAccountId, double amount, String description) {
        this.bankAccountId = bankAccountId;
        this.amount = amount;
        this.description = description;
    }
}
