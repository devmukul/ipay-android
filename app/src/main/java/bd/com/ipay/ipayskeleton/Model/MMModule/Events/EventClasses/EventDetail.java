package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class EventDetail {
    public long id;
    public String contactName;
    public String contactNumber;
    public String eventDescription;
    public String locationLattitude;
    public String locationLongitude;
    public String eventLink;
    public long creationDateTime;
    public long updateDateTime;

    public EventDetail() {
    }

    public long getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public String getLocationLattitude() {
        return locationLattitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public String getEventLink() {
        return eventLink;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }
}