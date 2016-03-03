package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class RestEventDetailDTO {

    public Long id;
    public String contactName;
    public String contactNumber;
    public String eventDetail;
    public String locationLattitude;
    public String locationLongitude;
    public String eventLink;

    public RestEventDetailDTO() {
    }

    public Long getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEventDetail() {
        return eventDetail;
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
}
