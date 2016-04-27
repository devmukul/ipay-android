package bd.com.ipay.ipayskeleton.Model.MMModule.ForgetPassword;

public class TrustedOtp {
    private long personId;
    private String otp;

    public TrustedOtp(long personId, String otp) {
        this.personId = personId;
        this.otp = otp;
    }
}
