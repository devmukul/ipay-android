package bd.com.ipay.ipayskeleton.Model.MMModule.Balance;

import java.math.BigDecimal;

public class RefreshBalanceResponse {

    public BigDecimal balance;
    public String statusDescription;

    public RefreshBalanceResponse() {

    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}
