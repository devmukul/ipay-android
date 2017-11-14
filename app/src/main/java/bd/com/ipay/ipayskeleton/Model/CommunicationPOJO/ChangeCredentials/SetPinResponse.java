package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class SetPinResponse {

    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }


    public SetPinResponse() {

    }

    public String getMessage() {
        return message;
    }
}
