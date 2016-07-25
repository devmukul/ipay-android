package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

public class RestEventParticipantDTO {

	private long id;

	private final String participantDetailedInformation ="";

	private String participantMobileNumber;

	private String participantName;

	private RestEventDTO event;

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
