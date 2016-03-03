package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class RestEventParticipantDTO {

	public long id;

	public String participantDetailedInformation ="";

	public String participantMobileNumber;

	public String participantName;

	public RestEventDTO event;

	public RestEventParticipantDTO() {
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

	public RestEventDTO getEvent() {
		return event;
	}
}
