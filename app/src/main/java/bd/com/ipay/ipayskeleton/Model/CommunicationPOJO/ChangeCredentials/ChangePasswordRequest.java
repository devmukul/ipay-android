package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class ChangePasswordRequest {

    private final String password;
    private final String newCredential;
    private String otp;

    public ChangePasswordRequest(String password, String newCredential, String otp) {
        this.password = password;
        this.newCredential = newCredential;
        this.otp = otp;
    }
}