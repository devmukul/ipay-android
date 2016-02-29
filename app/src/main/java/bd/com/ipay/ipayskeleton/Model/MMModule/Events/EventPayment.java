package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.math.BigDecimal;

public class EventPayment {

    public long id;
    public long deliveryTime;
    public long requestTime;
    public String receiverAccountId;
    public String senderAccountId;
    public String senderInformation;
    public String transactionId;
    public BigDecimal receiverAmount;
    public BigDecimal senderAmount;

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
