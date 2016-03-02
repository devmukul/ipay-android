package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

public class Participant {

    public long id;
    public String participantName;
    public String participantMobileNumber;
    public String participantDetailedInformation;

    public Participant(String participantName, String participantMobileNumber) {
        this.participantName = participantName;
        this.participantMobileNumber = participantMobileNumber;
    }

    public Participant(long id, String participantName, String participantMobileNumber, String participantDetailedInformation) {
        this.id = id;
        this.participantName = participantName;
        this.participantMobileNumber = participantMobileNumber;
        this.participantDetailedInformation = participantDetailedInformation;
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
}

