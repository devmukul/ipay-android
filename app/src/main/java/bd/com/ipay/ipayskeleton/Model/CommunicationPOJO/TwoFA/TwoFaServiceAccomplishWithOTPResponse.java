package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

/**
 * Created by sourav.saha on 9/19/17.
 */

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
