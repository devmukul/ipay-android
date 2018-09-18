package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

public class AmberItCustomerRequest {
    private String customerId;
    private String amount;

    public AmberItCustomerRequest(String customerId, String amount) {
        this.customerId = customerId;
        this.amount = amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
