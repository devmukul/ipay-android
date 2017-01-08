package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.ChangeCredentials;

public class ChangePasswordRequest {

    private final String password;
    private final String newCredential;

    public ChangePasswordRequest(String password, String newCredential) {
        this.password = password;
        this.newCredential = newCredential;
    }
}