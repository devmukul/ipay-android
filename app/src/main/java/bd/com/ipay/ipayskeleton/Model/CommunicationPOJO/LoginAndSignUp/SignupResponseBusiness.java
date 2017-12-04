package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class SignupResponseBusiness {

    private String message;
    private int accountType;
    private int[] accessControlList;

    public SignupResponseBusiness() {
    }

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
