package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

class EventParticipant {
    private long id;
    private String participantDetailedInformation;
    private String participantMobileNumber;
    private String participantName;
    private Event event;
    private long creationDateTime;
    private long updateDateTime;

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


