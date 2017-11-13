package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class SetPinResponse {

    private String message;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    private long otpValidFor;

    public SetPinResponse() {

    }

    public String getMessage() {
        return message;
    }
}
