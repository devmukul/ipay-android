package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Security;

public class OTPRequestChangePassword {
    private final String password;
    private final String newCredential;

    public OTPRequestChangePassword(String password, String newCredential) {
        this.password = password;
        this.newCredential = newCredential;
    }
}
