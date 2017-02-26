package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class ChangePasswordValidationResponse {

    private String message;
    private long otpValidFor;

    public ChangePasswordValidationResponse() {

    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
