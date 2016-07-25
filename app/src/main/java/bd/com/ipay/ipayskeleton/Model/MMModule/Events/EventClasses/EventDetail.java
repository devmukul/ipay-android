package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class EventDetail {
    private long id;
    private String contactName;
    private String contactNumber;
    private String eventDescription;
    private String locationLattitude;
    private String locationLongitude;
    private String eventLink;
    private long creationDateTime;
    private long updateDateTime;

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