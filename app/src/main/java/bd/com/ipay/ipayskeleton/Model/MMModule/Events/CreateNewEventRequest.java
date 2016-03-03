package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.RestEventCategoryDTO;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.RestEventCreatorDTO;
import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.RestEventParticipantDTO;

public class CreateNewEventRequest {

    private Long id;
    private BigDecimal charge;
    private Long endTime;
    private Integer maxNumOfParticipants;
    private String name;
    private Integer selectedParticipantsType;
    private Long startTime;
    private RestEventCategoryDTO eventCategoryBean;
    private RestEventCreatorDTO eventCreatorBean;
    private List<RestEventParticipantDTO> eventParticipantList;
    private int status;
    private String contactName;
    private String contactNumber;
    private String eventDescription;
    private String eventLink;
    private String locationLattitude = "Dhaka";
    private String locationLongitude = "Dhaka";
    private int maxNumberFromOneAccount = 1;
    private String accountName;

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
