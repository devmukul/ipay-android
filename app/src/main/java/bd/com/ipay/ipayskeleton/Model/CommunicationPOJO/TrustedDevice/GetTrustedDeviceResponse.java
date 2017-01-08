package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TrustedDevice;

import java.util.List;

public class GetTrustedDeviceResponse {
    private String message;
    private List<TrustedDevice> devices;

    public GetTrustedDeviceResponse() {

    }

    public String getMessage() {
        return message;
    }

    public List<TrustedDevice> getDevices() {
        return devices;
    }
}
