package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Email;

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

    @Override
    public String toString() {
        return "Email{" +
                "emailId=" + emailId +
                ", emailAddress='" + emailAddress + '\'' +
                ", primary=" + primary +
                ", verificationStatus='" + verificationStatus + '\'' +
                '}';
    }
}
