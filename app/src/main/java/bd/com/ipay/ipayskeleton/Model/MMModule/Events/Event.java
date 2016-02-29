package bd.com.ipay.ipayskeleton.Model.MMModule.Events;


import java.math.BigDecimal;

public class Event {

    public long id;
    public BigDecimal charge;
    public long endTime;
    public String name;
    public long startTime;

    public long getId() {
        return id;
    }

    public BigDecimal getCharge() {
        return charge;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }
}
