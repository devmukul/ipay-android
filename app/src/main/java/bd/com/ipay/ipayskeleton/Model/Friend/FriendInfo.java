package bd.com.ipay.ipayskeleton.Model.Friend;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;

public class FriendInfo {
    private int accountType;
    private String mobileNumber;
    private boolean iPayMember;
    private boolean verified;
    private String contactName; // Previous name
    private String iPayName; // Previous original name
    private ProfilePictureURL profilePictureURL;
    private String relationship;
    private long updateAt;
    private boolean active;


    public FriendInfo(String name, String mobileNumber, String profilePictureUrl) {
        this.contactName = name;
        this.mobileNumber = mobileNumber;
        this.profilePictureURL = new ProfilePictureURL(profilePictureUrl);
    }

    public FriendInfo(int accountType, boolean isMember, boolean isVerified, String name, String originalName, String mobileNumber,
                      String profilePictureUrl, String profilePictureUrlMedium, String profilePictureUrlHigh, String relationship, long updateAt) {
        this.accountType = accountType;
        this.mobileNumber = mobileNumber;
        this.iPayMember = isMember;
        this.verified = isVerified;
        this.contactName = name;
        this.iPayName = originalName;
        this.profilePictureURL = new ProfilePictureURL(profilePictureUrl, profilePictureUrlMedium, profilePictureUrlHigh);
        this.relationship = relationship;
        this.updateAt = updateAt;
    }

    public FriendInfo(int accountType, int isMember, int verificationStatus, String name, String originalName,String mobileNumber,
                      String profilePictureUrl, String profilePictureUrlMedium, String profilePictureUrlHigh, String relationship, long updateAt) {
        this(accountType, isMember == DBConstants.IPAY_MEMBER, verificationStatus == DBConstants.VERIFIED_USER,
                name, originalName,mobileNumber, profilePictureUrl, profilePictureUrlMedium, profilePictureUrlHigh, relationship, updateAt);
    }

    public int getAccountType() {
        return accountType;
    }


    public String getMobileNumber() {
        return mobileNumber;
    }

    public boolean isMember() {
        return iPayMember;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getName() {
        return contactName;
    }

    public String getOriginalName() {
        return iPayName;
    }

    public long getUpdateTime() {
        return updateAt;
    }

    public boolean isActive() {
        return active;
    }

    public String getProfilePictureUrl() {
        return profilePictureURL.getLow();
    }

    // TODO remove extra checking once medium quality profile picture becomes available in live
    public String getProfilePictureUrlMedium() {
        if (profilePictureURL.getMedium() != null && !profilePictureURL.getMedium().isEmpty())
            return profilePictureURL.getMedium();
        else
            return getProfilePictureUrl();
    }

    // TODO remove extra checking once high quality profile picture becomes available in live
    public String getProfilePictureUrlHigh() {
        if (profilePictureURL.getHigh() != null && !profilePictureURL.getHigh().isEmpty())
            return profilePictureURL.getHigh();
        else
            return getProfilePictureUrlMedium();
    }

    public String getRelationship() {
        return relationship;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public void setVerified(boolean verified) {
        verified = verified;
    }

    public void setName(String name) {
        this.contactName = name;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureURL =new ProfilePictureURL(profilePictureUrl);
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "accountType=" + accountType +
                ", isMember=" + iPayMember +
                ", isVerified=" + verified +
                ", name='" + contactName + '\'' +
                ", originalName='" + iPayName + '\'' +
                ", profilePictureUrl='" + profilePictureURL + '\'' +
                '}';
    }
}
