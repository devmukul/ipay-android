package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.math.BigDecimal;
import java.util.List;

public class RestEventDTO {
	
	public long id;
	public BigDecimal charge;
	public long endTime;
	public int maxNumOfParticipants;
	public String name;
	public int selectedParticipantsType;
	public long startTime;
	public RestEventCategoryDTO eventCategoryBean;
	public RestEventCreatorDTO eventCreatorBean;
	public List<RestEventParticipantDTO>eventParticipantList;
	public int status;
	public String contactName;
	public String  contactNumber ;
	public String  eventDescription;
	public String  eventLink;
	public String locationLattitude = "Dhaka";
	public String locationLongitude= "Dhaka";
	public int maxNumberFromOneAccount = 1;
	public String  accountName;

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
