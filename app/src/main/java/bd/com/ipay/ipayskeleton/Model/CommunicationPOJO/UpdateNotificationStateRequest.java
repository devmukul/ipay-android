package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;

import java.util.List;

public class UpdateNotificationStateRequest {
    private List<Long> timeList;
    private String status;

    public UpdateNotificationStateRequest(List<Long> timeList, String status) {
        this.timeList = timeList;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<Long> timeList) {
        this.timeList = timeList;
    }
}
