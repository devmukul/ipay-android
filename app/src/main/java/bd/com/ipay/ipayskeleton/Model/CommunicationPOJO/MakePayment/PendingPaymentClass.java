package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.UserProfile;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PendingPaymentClass {

    private Long id;
    private BigDecimal amount;
    private Long requestTime;
    private String title;
    private Long serviceID;
    private String transactionID;
    private String description;
    private UserProfile originatorProfile;
    private UserProfile receiverProfile;
    private int status;
    private BigDecimal vat;
    private BigDecimal total;
    private InvoiceItem[] itemList;


    public InvoiceItem[] getItemList() {
        return itemList;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public BigDecimal getTotal() {
        return total;
    }


    public PendingPaymentClass() {
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

    public String getTransactionID() {
        return transactionID;
    }

    public int getStatus() {
        return status;
    }

    public String getDescriptionOfRequest() {
        return description;
    }

    public String getCustomizedDescription() {

        String customizedDescription = "";

        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) {
            customizedDescription = getOriginatorProfile().getUserName() + " requested " + amount + " Tk.";
        } else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) {
            customizedDescription = getOriginatorProfile().getUserName() + " sent an invoice of " + amount + " Tk. to " + getReceiverProfile().getUserName();
        }

        return customizedDescription;
    }
}
