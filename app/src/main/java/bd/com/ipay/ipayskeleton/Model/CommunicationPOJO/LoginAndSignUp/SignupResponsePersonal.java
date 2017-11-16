package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class SignupResponsePersonal {

    private String message;
    private int accountType;
    private int[] accessControlList;

    public String getMessage() {
        return message;
    }

    public int getAccountType() {
        return accountType;
    }

    public int[] getAccessControlList() {
        return accessControlList;
    }
}
