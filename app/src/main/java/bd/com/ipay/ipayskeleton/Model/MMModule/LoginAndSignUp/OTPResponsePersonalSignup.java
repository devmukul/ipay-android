package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;


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
