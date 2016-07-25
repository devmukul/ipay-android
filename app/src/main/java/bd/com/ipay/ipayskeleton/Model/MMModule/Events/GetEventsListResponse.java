package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.Event;

class GetEventsListResponse {

    private List<Event> eventList;

    public GetEventsListResponse() {
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
