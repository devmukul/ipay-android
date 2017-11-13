package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.TwoFA.TwoFAServiceGroup;

public class TwoFAServiceListResponse {
    private List<TwoFAServiceGroup> response;
    private String message;

    public TwoFAServiceListResponse() {
    }

    public TwoFAServiceListResponse(List<TwoFAServiceGroup> response) {

        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TwoFAServiceGroup> getResponse() {

        return response;
    }

    public void setResponse(List<TwoFAServiceGroup> response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "TwoFAServiceListResponse{" +
                "response=" + response +
                '}';
    }
}
