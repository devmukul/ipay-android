package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.UserProfile;

public class MoneyRequestClass {

    private Long id;
    private BigDecimal amount;
    private Long requestTime;
    private String title;
    private Long serviceID;
    private String description;
    private UserProfile originatorProfile;
    private UserProfile receiverProfile;
    private int status;
    private String transactionID;

    public MoneyRequestClass() {
    }

    public Long getId() {
        return id;
    }


    public BigDecimal getAmount() {
        return amount;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public String getTitle() {
        return title;
    }

    public UserProfile getOriginatorProfile() {
        return originatorProfile;
    }

    public UserProfile getReceiverProfile() {
        return receiverProfile;
    }

    public Long getServiceID() {
        return serviceID;
    }

    public int getStatus() {
        return status;
    }

    public String getTransactionID() {
        return transactionID;
    }


    public String getDescription() {
        return description;
    }
}
