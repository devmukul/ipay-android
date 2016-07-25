package bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials;

public class SetPinRequest {

    private final String password;
    private final String newCredential;

    public SetPinRequest(String newCredential, String password) {
        this.password = password;
        this.newCredential = newCredential;
    }
}