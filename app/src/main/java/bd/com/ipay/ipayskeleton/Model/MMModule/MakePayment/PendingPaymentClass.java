package bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.UserProfile;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PendingPaymentClass {

    public Long id;
    public BigDecimal amount;
    public Long requestTime;
    public String title;
    public Long serviceID;
    public String description;
    public UserProfile originatorProfile;
    public UserProfile receiverProfile;
    public int status;
    public BigDecimal vat;
    public BigDecimal total;
    public BigDecimal rate;
    public BigDecimal quantity;
    public String item;
    public String ItemDescription;


    public String getItem() {
        return item;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal getQuantity() {
        return quantity;
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
        if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) return "Invoice received";
        else if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) return title;
        else return title;
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

    public int getStatus() { return status; }

    public String getDescription() {

        String customDescription = "";

        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) {
            customDescription = getOriginatorProfile().getUserName() + " requested " + amount + " Tk.";
        } else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) {
            customDescription = getOriginatorProfile().getUserName() + " sent an invoice of " + amount + " Tk. to " + getReceiverProfile().getUserName();
        }

        return customDescription;
    }
}
