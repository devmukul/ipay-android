package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp;

public class LoginResponse {

    private String message;
    private int accountType;
    private long otpValidFor;
    private int[] accessControlList;

    public LoginResponse() {
    }

    public long getOtpValidFor() {
        return otpValidFor;
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
