package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class Participant {

    public long id;
    public String participantName;
    public String participantMobileNumber;
    public String participantDetailedInformation;
    public long creationDateTime;
    public long updateDateTime;

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

