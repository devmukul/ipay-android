package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class EventParticipant {
    public long id;
    public String participantDetailedInformation;
    public String participantMobileNumber;
    public String participantName;
    public Event event;
    public long creationDateTime;
    public long updateDateTime;

    public EventParticipant() {
    }

    public long getId() {
        return id;
    }

    public String getParticipantDetailedInformation() {
        return participantDetailedInformation;
    }

    public String getParticipantMobileNumber() {
        return participantMobileNumber;
    }

    public String getParticipantName() {
        return participantName;
    }

    public Event getEvent() {
        return event;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }
}


