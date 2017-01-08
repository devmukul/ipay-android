package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class OTPResponseBusinessSignup {
    private String message;
    private long otpValidFor;

    public OTPResponseBusinessSignup() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
