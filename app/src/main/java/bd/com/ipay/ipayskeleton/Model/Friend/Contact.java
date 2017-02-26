package bd.com.ipay.ipayskeleton.Model.Friend;

public class Contact {
    private String name;
    private String originalName;
    private String mobileNumber;
    private String profilePictureUrlQualityMedium;
    private int verificationStatus;
    private int accountType;
    private int memberStatus;
    private boolean isInvited;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfilePictureUrlQualityMedium() {
        return profilePictureUrlQualityMedium;
    }

    public void setProfilePictureUrlQualityMedium(String profilePictureUrlQualityMedium) {
        this.profilePictureUrlQualityMedium = profilePictureUrlQualityMedium;
    }

    public int getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(int verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public int getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(int memberStatus) {
        this.memberStatus = memberStatus;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }
}
