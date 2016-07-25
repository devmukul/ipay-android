package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

class PendingMoneyRequestClass {

    private Long id;
    private String senderMobileNumber;
    private String receiverMobileNumber;
    private double amount;
    private Long requestTime;
    private String title;
    private String description;

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
