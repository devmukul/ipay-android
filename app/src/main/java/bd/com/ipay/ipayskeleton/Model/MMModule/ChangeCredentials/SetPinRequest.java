package bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials;

public class SetPinRequest {

    private String password;
    private String newCredential;

    public SetPinRequest(String password, String newCredential) {
        this.password = password;
        this.newCredential = newCredential;
    }
}