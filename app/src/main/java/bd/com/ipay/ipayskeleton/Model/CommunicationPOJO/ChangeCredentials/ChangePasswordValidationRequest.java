package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class ChangePasswordValidationRequest {
    private final String password;
    private final String newCredential;

    public ChangePasswordValidationRequest(String password, String newCredential) {
        this.password = password;
        this.newCredential = newCredential;
    }
}
