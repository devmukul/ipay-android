package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ForgetPassword;

import java.util.List;

public class ForgetPassOTPConfirmationRequest {

    private final String mobileNumber;
    private final String deviceId;
    private final String otp;
    private final String newPassword;
    private final List<TrustedOtp> trustedOtps;

    public ForgetPassOTPConfirmationRequest(String mobileNumber, String deviceId, String otp, String newPassword, List<TrustedOtp> trustedOtps) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.otp = otp;
        this.newPassword = newPassword;
        this.trustedOtps = trustedOtps;
    }
}
