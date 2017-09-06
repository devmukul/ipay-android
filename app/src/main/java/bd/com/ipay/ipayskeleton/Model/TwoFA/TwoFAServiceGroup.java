package bd.com.ipay.ipayskeleton.Model.TwoFA;

import java.util.List;

public class TwoFAServiceGroup {

    private String groupName;
    private List<TwoFAService> services;

    public TwoFAServiceGroup() {
    }

    public TwoFAServiceGroup(String groupName, List<TwoFAService> services) {

        this.groupName = groupName;
        this.services = services;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<TwoFAService> getServices() {
        return services;
    }

    public void setServices(List<TwoFAService> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "TwoFAServiceGroup{" +
                "groupName='" + groupName + '\'' +
                ", services=" + services +
                '}';
    }
}
