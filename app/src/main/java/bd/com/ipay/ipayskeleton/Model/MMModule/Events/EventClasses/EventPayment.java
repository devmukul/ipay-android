package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.math.BigDecimal;

public class EventPayment {

    private long id;
    private long deliveryTime;
    private long requestTime;
    private String receiverAccountId;
    private String senderAccountId;
    private String senderInformation;
    private String transactionId;
    private BigDecimal receiverAmount;
    private BigDecimal senderAmount;

    public long getId() {
        return id;
    }

    public long getDeliveryTime() {
        return deliveryTime;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public String getSenderInformation() {
        return senderInformation;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public BigDecimal getReceiverAmount() {
        return receiverAmount;
    }

    public BigDecimal getSenderAmount() {
        return senderAmount;
    }
}
