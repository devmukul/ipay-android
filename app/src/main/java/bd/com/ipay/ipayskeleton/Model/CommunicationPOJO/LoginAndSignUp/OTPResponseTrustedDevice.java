package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;


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

