package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Security;

public class OTPResponseChangePassword {

    private String message;
    private long otpValidFor;

    public OTPResponseChangePassword() {

    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
