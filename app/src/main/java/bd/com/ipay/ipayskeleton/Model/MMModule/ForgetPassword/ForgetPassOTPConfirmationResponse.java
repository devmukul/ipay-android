package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

public class ForgetPassOTPConfirmationResponse {

    private String message;
    private long otpValidFor;

    public ForgetPassOTPConfirmationResponse() {

    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }
}
