package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.Event;

public class GetEventListPersonalUserResponse {

    public ArrayList<Event> eventList = new ArrayList<Event>();

    public GetEventListPersonalUserResponse() {
    }

    public ArrayList<Event> getEventList() {
        return eventList;
    }
}
