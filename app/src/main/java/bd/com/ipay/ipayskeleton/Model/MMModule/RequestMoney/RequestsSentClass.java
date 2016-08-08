package bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.UserProfile;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class RequestsSentClass {

    private Long id;
    private BigDecimal amount;
    private Long requestTime;
    private String title;
    private Long serviceID;
    private String description;
    private UserProfile originatorProfile;
    private UserProfile receiverProfile;

    public RequestsSentClass() {
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

        if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) return title;
        else if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) return "Money requested";
        else return title;
    }

    private UserProfile getOriginatorProfile() {
        return originatorProfile;
    }

    public UserProfile getReceiverProfile() {
        return receiverProfile;
    }

    public Long getServiceID() {
        return serviceID;
    }

    public String getDescription() {

        String customDescription = "";

        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY) {
            customDescription = getOriginatorProfile().getUserName() + " requested " + amount + " Tk.";
        } else if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE) {
            customDescription = description + ": " + getOriginatorProfile().getUserName() + " sent an invoice of " + amount + " Tk.";
        }

        return customDescription;
    }
}
