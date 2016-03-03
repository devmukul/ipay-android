package bd.com.ipay.ipayskeleton.Model.MMModule.Events;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.Events.EventClasses.Participant;

public class GetAllParticipantsResponse {

    private List<Participant> participants;
    private boolean hasNext;

    public GetAllParticipantsResponse() {

    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public boolean isHasNext() {
        return hasNext;
    }
}
