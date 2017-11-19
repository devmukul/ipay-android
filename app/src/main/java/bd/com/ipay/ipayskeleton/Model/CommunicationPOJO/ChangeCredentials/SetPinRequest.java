package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class SetPinRequest {

    private final String password;
    private final String newCredential;
    private String otp;

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public SetPinRequest(String newCredential, String password) {
        this.password = password;
        this.newCredential = newCredential;
    }
}