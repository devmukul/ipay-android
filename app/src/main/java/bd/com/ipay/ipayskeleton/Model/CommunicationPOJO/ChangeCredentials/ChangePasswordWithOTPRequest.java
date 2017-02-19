package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class ChangePasswordWithOTPRequest {

    private final String password;
    private final String newCredential;
    private String otp;

    public ChangePasswordWithOTPRequest(String password, String newCredential, String otp) {
        this.password = password;
        this.newCredential = newCredential;
        this.otp = otp;
    }
}