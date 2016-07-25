package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.Event;

class GetEventListPersonalUserResponse {

    private final ArrayList<Event> eventList = new ArrayList<>();

    public GetEventListPersonalUserResponse() {
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }
}
