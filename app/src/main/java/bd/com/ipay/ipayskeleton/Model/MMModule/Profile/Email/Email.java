package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Email;

public class Email {
    private long emailId;
    private String emailAddress;
    private boolean primary;
    private String verificationStatus;

    public long getEmailId() {
        return emailId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isPrimary() {
        return primary;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }
}
