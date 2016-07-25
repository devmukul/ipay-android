package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.io.Serializable;

public class EventCreator implements Serializable {
    private long id;
    private String ipayAccountId;
    private String ipayAccountName;
    private long creationDateTime;
    private long updateDateTime;

    public EventCreator() {
    }

    public long getId() {
        return id;
    }

    public String getIpayAccountId() {
        return ipayAccountId;
    }

    public String getIpayAccountName() {
        return ipayAccountName;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }
}