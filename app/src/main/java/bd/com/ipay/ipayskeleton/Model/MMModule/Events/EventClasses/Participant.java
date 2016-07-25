package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class Participant {

    private long id;
    private String participantName;
    private String participantMobileNumber;
    private String participantDetailedInformation;
    private long creationDateTime;
    private long updateDateTime;

    public Participant() {
    }

    public long getId() {
        return id;
    }

    public String getParticipantName() {
        return participantName;
    }

    public String getParticipantMobileNumber() {
        return participantMobileNumber;
    }

    public String getParticipantDetailedInformation() {
        return participantDetailedInformation;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public void setParticipantMobileNumber(String participantMobileNumber) {
        this.participantMobileNumber = participantMobileNumber;
    }

    public void setParticipantDetailedInformation(String participantDetailedInformation) {
        this.participantDetailedInformation = participantDetailedInformation;
    }

    public void setCreationDateTime(long creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public void setUpdateDateTime(long updateDateTime) {
        this.updateDateTime = updateDateTime;
    }
}

