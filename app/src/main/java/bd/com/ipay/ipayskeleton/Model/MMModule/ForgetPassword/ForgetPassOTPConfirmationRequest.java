package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

import java.util.List;

public class ForgetPassOTPConfirmationRequest {

    private String mobileNumber;
    private String deviceId;
    private String otp;
    private String newPassword;
    private List<TrustedOtp> trustedOtps;

    public ForgetPassOTPConfirmationRequest(String mobileNumber, String deviceId, String otp, String newPassword, List<TrustedOtp> trustedOtps) {
        this.mobileNumber = mobileNumber;
        this.deviceId = deviceId;
        this.otp = otp;
        this.newPassword = newPassword;
        this.trustedOtps = trustedOtps;
    }
}
