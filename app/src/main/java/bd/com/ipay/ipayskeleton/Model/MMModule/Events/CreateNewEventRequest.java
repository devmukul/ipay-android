package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.RestEventCategoryDTO;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.RestEventCreatorDTO;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.RestEventParticipantDTO;

class CreateNewEventRequest {

    private final Long id;
    private final BigDecimal charge;
    private final Long endTime;
    private final Integer maxNumOfParticipants;
    private final String name;
    private final Integer selectedParticipantsType;
    private final Long startTime;
    private final RestEventCategoryDTO eventCategoryBean;
    private final RestEventCreatorDTO eventCreatorBean;
    private final List<RestEventParticipantDTO> eventParticipantList;
    private final int status;
    private final String contactName;
    private final String contactNumber;
    private final String eventDescription;
    private final String eventLink;
    private String locationLattitude = "Dhaka";
    private String locationLongitude = "Dhaka";
    private int maxNumberFromOneAccount = 1;
    private final String accountName;

    public CreateNewEventRequest(Long id, BigDecimal charge, Long endTime, Integer maxNumOfParticipants, String name, Integer selectedParticipantsType, Long startTime, RestEventCategoryDTO eventCategoryBean, RestEventCreatorDTO eventCreatorBean, List<RestEventParticipantDTO> eventParticipantList, int status, String contactName, String contactNumber, String eventDescription, String eventLink, String locationLattitude, String locationLongitude, int maxNumberFromOneAccount, String accountName) {
        this.id = id;
        this.charge = charge;
        this.endTime = endTime;
        this.maxNumOfParticipants = maxNumOfParticipants;
        this.name = name;
        this.selectedParticipantsType = selectedParticipantsType;
        this.startTime = startTime;
        this.eventCategoryBean = eventCategoryBean;
        this.eventCreatorBean = eventCreatorBean;
        this.eventParticipantList = eventParticipantList;
        this.status = status;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
        this.eventDescription = eventDescription;
        this.eventLink = eventLink;
        this.locationLattitude = locationLattitude;
        this.locationLongitude = locationLongitude;
        this.maxNumberFromOneAccount = maxNumberFromOneAccount;
        this.accountName = accountName;
    }
}
