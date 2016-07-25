package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.math.BigDecimal;
import java.util.List;

public class Event {

    private long id;
    private BigDecimal charge;
    private long endTime;
    private int maxNumOfParticipants;
    private int maxNumberFromOneAccount;
    private int selectedParticipantsType;
    private long startTime;
    private EventCategory eventCategoryBean;
    private EventCreator eventCreatorBean;
    private EventDetail eventDetailBean;
    private EventStatus eventStatusBean;
    private List<EventParticipant> eventParticipants;
    private List<EventPayment> eventPayments;
    private long creationDateTime;
    private long updateDateTime;

    public Event() {
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

    public int getMaxNumberFromOneAccount() {
        return maxNumberFromOneAccount;
    }

    public int getSelectedParticipantsType() {
        return selectedParticipantsType;
    }

    public long getStartTime() {
        return startTime;
    }

    public EventCategory getEventCategoryBean() {
        return eventCategoryBean;
    }

    public EventCreator getEventCreatorBean() {
        return eventCreatorBean;
    }

    public EventDetail getEventDetailBean() {
        return eventDetailBean;
    }

    public EventStatus getEventStatusBean() {
        return eventStatusBean;
    }

    public List<EventParticipant> getEventParticipants() {
        return eventParticipants;
    }

    public List<EventPayment> getEventPayments() {
        return eventPayments;
    }

    public long getCreationDateTime() {
        return creationDateTime;
    }

    public long getUpdateDateTime() {
        return updateDateTime;
    }
}
