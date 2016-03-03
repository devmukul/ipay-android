package bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses;

import java.math.BigDecimal;
import java.util.List;

public class Event {

    public long id;
    public BigDecimal charge;
    public long endTime;
    public int maxNumOfParticipants;
    public int maxNumberFromOneAccount;
    public int selectedParticipantsType;
    public long startTime;
    public EventCategory eventCategoryBean;
    public EventCreator eventCreatorBean;
    public EventDetail eventDetailBean;
    public EventStatus eventStatusBean;
    public List<EventParticipant> eventParticipants;
    public List<EventPayment> eventPayments;
    public long creationDateTime;
    public long updateDateTime;

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
