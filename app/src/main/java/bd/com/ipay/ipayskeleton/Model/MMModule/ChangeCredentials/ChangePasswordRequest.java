package bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials;

public class ChangePasswordRequest {

    private String password;
    private String newCredential;

    public ChangePasswordRequest(String password, String newCredential) {
        this.password = password;
        this.newCredential = newCredential;
    }
}