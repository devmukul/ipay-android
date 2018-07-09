package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;

import java.util.List;

public class UpdateNotificationStateRequest {
    private List<Long> timeList;

    public UpdateNotificationStateRequest(List<Long> timeList) {
        this.timeList = timeList;
    }

    public List<Long> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<Long> timeList) {
        this.timeList = timeList;
    }
}
