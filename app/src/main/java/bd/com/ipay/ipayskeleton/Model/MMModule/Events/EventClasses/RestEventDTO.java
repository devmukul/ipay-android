package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.math.BigDecimal;
import java.util.List;

public class RestEventDTO {
	
	private long id;
	private BigDecimal charge;
	private long endTime;
	private int maxNumOfParticipants;
	private String name;
	private int selectedParticipantsType;
	private long startTime;
	private RestEventCategoryDTO eventCategoryBean;
	private RestEventCreatorDTO eventCreatorBean;
	private List<RestEventParticipantDTO>eventParticipantList;
	private int status;
	private String contactName;
	private String  contactNumber ;
	private String  eventDescription;
	private String  eventLink;
	private final String locationLattitude = "Dhaka";
	private final String locationLongitude= "Dhaka";
	private final int maxNumberFromOneAccount = 1;
	private String  accountName;

	public RestEventDTO() {
	}

	public long getId() {
		return id;
	}

	public BigDecimal getCharge() {
		return charge;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getMaxNumOfParticipants() {
		return maxNumOfParticipants;
	}

	public String getName() {
		return name;
	}

	public int getSelectedParticipantsType() {
		return selectedParticipantsType;
	}

	public long getStartTime() {
		return startTime;
	}

	public RestEventCategoryDTO getEventCategoryBean() {
		return eventCategoryBean;
	}

	public RestEventCreatorDTO getEventCreatorBean() {
		return eventCreatorBean;
	}

	public List<RestEventParticipantDTO> getEventParticipantList() {
		return eventParticipantList;
	}

	public int getStatus() {
		return status;
	}

	public String getContactName() {
		return contactName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public String getEventDescription() {
		return eventDescription;
	}

	public String getEventLink() {
		return eventLink;
	}

	public String getLocationLattitude() {
		return locationLattitude;
	}

	public String getLocationLongitude() {
		return locationLongitude;
	}

	public int getMaxNumberFromOneAccount() {
		return maxNumberFromOneAccount;
	}

	public String getAccountName() {
		return accountName;
	}
}
