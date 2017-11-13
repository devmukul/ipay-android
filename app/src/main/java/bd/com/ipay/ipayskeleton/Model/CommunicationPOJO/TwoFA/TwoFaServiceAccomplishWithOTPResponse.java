package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

public class TwoFaServiceAccomplishWithOTPResponse {

    private String message;

    public TwoFaServiceAccomplishWithOTPResponse(String message) {
        this.message = message;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
