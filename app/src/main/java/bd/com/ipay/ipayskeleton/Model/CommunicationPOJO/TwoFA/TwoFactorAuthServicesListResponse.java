package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFactorAuthServiceGroup;

public class TwoFactorAuthServicesListResponse {
    private List<TwoFactorAuthServiceGroup> response;
    private String message;

    public TwoFactorAuthServicesListResponse() {
    }

    public TwoFactorAuthServicesListResponse(List<TwoFactorAuthServiceGroup> response) {

        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TwoFactorAuthServiceGroup> getResponse() {

        return response;
    }

    public void setResponse(List<TwoFactorAuthServiceGroup> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "TwoFactorAuthServicesListResponse{" +
                "response=" + response +
                '}';
    }
}
