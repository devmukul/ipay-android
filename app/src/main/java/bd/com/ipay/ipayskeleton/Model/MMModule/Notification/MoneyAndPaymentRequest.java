package bd.com.ipay.ipayskeleton.Model.MMModule.Notification;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.MakePayment.ItemList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MoneyAndPaymentRequest implements Notification {

    private Long id;
    private BigDecimal amount;
    private Long requestTime;
    private String title;
    private int serviceID;
    public String description;
    public UserProfile originatorProfile;
    private UserProfile receiverProfile;
    private BigDecimal vat;
    private List<ItemList> itemList;
    private int status;


    public MoneyAndPaymentRequest() {
    }

    public List<ItemList> getItemList() {
        return itemList;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return getOriginatorProfile().getUserName();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public String getNotificationTitle() {
        if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) return "Payment Request";
        else if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) return "Money Request";
        else return title;
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

    public int getServiceID() {
        return serviceID;
    }

    @Override
    public int getNotificationType() {
        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY)
            return Constants.NOTIFICATION_TYPE_REQUEST_MONEY;
        else
            return Constants.NOTIFICATION_TYPE_MAKE_PAYMENT;
    }

    public String getDescriptionofRequest() {
        return description;
    }

    @Override
    public String getDescription() {

        String customDescription = "";

        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) {
            customDescription = getOriginatorProfile().getUserName() + " requested " + amount + " Tk.";
        } else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) {
            customDescription = getOriginatorProfile().getUserName() + " sent an invoice of " + amount + " Tk.";
        }

        return customDescription;
    }

    @Override
    public String getImageUrl() {
        return getOriginatorProfile().getUserProfilePicture();
    }

    @Override
    public long getTime() {
        return getRequestTime();
    }

    public int getStatus() {
        return status;
    }
}
