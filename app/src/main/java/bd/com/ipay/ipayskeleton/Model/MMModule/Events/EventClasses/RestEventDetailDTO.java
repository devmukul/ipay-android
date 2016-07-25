package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

class RestEventDetailDTO {

    private Long id;
    private String contactName;
    private String contactNumber;
    private String eventDetail;
    private String locationLattitude;
    private String locationLongitude;
    private String eventLink;

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
