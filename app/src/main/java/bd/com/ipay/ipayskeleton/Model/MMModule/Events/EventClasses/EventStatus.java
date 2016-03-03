package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class EventStatus {
    private long id;
    private long enrolledNumber;
    private int eventCurrentStatus;
    private long creationDateTime;
    private long updateDateTime;

    public EventStatus() {
    }

    public long getId() {
        return id;
    }

    public long getEnrolledNumber() {
        return enrolledNumber;
    }

    public int getEventCurrentStatus() {
        return eventCurrentStatus;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }
}