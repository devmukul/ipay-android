package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

public class ForgetPasswordResponse {

    private String message;
    private long otpValidFor;

    public ForgetPasswordResponse() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
