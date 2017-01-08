package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Balance;

import java.math.BigDecimal;

public class RefreshBalanceResponse {

    private BigDecimal balance;
    private String statusDescription;

    public RefreshBalanceResponse() {

    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatusDescription() {
        return statusDescription;
    }
}
