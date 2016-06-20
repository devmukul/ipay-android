package bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite;

public class IntroductionRequestClass {
    public long id;
    public String senderMobileNumber;
    public String senderName;
    public long senderAccountId;
    public long verifierAccountId;
    public long date;
    public String status;
    public String profilePictureUrl;

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public long getId() {
        return id;
    }

    public String getSenderMobileNumber() {
        return senderMobileNumber;
    }

    public String getSenderName() {
        return senderName;
    }

    public long getSenderAccountId() {
        return senderAccountId;
    }

    public long getVerifierAccountId() {
        return verifierAccountId;
    }

    public long getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }
}
