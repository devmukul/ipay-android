package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class NotificationClass {

    public Long id;
    public BigDecimal amount;
    public Long requestTime;
    public String title;
    public int serviceID;
    public String description;
    public UserProfile originatorProfile;
    public UserProfile receiverProfile;
    public BigDecimal vat;
    public ItemList[] itemList;


    public NotificationClass() {
    }

    public ItemList[] getItemList() {
        return itemList;
    }

    public BigDecimal getVat() {
        return vat;
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

    public int getServiceID() {
        return serviceID;
    }

    public String getDescription() {

        String customDescription = "";

        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) {
            customDescription = getOriginatorProfile().getUserName() + " requested " + amount + " Tk.";
        } else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) {
            customDescription = getOriginatorProfile().getUserName() + " sent an invoice of " + amount + " Tk.";
        }

        return customDescription;
    }
}
