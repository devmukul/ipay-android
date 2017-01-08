package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;


public class OTPResponsePersonalSignup {
    private String message;
    private long otpValidFor;

    public OTPResponsePersonalSignup() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
