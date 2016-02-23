package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

public class PendingPaymentClass {

    public Long id;
    public String senderMobileNumber;
    public String receiverMobileNumber;
    public double amount;
    public Long requestTime;
    public String title;
    public String description;

    public Long getId() {
        return id;
    }

    public String getSenderMobileNumber() {
        return senderMobileNumber;
    }

    public double getAmount() {
        return amount;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReceiverMobileNumber() {
        return receiverMobileNumber;
    }
}
