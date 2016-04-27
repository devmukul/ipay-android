package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

import java.util.ArrayList;

public class ForgetPasswordResponse {

    private String message;
    private long otpValidFor;
    private ArrayList<TrustedOtpReceiver> trustedOtpReceivers;

    public ForgetPasswordResponse() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<TrustedOtpReceiver> getTrustedOtpReceiverList() {
        return trustedOtpReceivers;
    }
}
