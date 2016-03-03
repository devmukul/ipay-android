package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.EventPayment;

public class GetEventPaymentsListResponse {

    private List<EventPayment> eventPayments;

    public GetEventPaymentsListResponse() {
    }

    public List<EventPayment> getEventPayments() {
        return eventPayments;
    }
}
