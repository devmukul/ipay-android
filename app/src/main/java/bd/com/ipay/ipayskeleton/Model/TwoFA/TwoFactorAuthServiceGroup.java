package bd.com.ipay.ipayskeleton.Model.TwoFA;

import java.util.List;

public class TwoFactorAuthServiceGroup {

    private String groupName;
    private List<TwoFactorAuthService> services;

    public TwoFactorAuthServiceGroup() {
    }

    public TwoFactorAuthServiceGroup(String groupName, List<TwoFactorAuthService> services) {

        this.groupName = groupName;
        this.services = services;
    }

    public String getGroupName() {

        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<TwoFactorAuthService> getServices() {
        return services;
    }

    public void setServices(List<TwoFactorAuthService> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "TwoFactorAuthServiceGroup{" +
                "groupName='" + groupName + '\'' +
                ", services=" + services +
                '}';
    }
}
