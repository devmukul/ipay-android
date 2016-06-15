package bd.com.ipay.ipayskeleton.Model.MMModule.LoginAndSignUp;


public class OTPResponseTrustedDevice {

    private String message;
    private long otpValidFor;

    public OTPResponseTrustedDevice(){

    }

    public long getOtpValidFor() {
        return otpValidFor;
    }
    public String getMessage() {
        return message;
    }
}

