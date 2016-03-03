package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.io.Serializable;

public class EventCreator implements Serializable {
    public long id;
    public String ipayAccountId;
    public String ipayAccountName;
    public long creationDateTime;
    public long updateDateTime;

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