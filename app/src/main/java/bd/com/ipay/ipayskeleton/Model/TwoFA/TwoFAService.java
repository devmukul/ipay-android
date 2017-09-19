package bd.com.ipay.ipayskeleton.Model.TwoFA;

public class TwoFAService {

    private int serviceId;
    private String serviceName;
    private boolean isEnabled;

    public TwoFAService() {
    }

    public TwoFAService(int serviceId, String serviceName, boolean isEnabled) {

        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.isEnabled = isEnabled;
    }

    public int getServiceId() {

        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public String toString() {
        return "TwoFAService{" +
                "serviceId='" + serviceId + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", isEnabled='" + isEnabled + '\'' +
                '}';
    }
}
