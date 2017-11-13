package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TwoFA;

public class TwoFactorAuthSettingsSaveResponse {
    private String message;
    private long otpValidFor;

    public long getOtpValidFor() {
        return otpValidFor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
