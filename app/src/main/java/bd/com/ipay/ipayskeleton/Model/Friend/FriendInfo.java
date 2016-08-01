package bd.com.ipay.ipayskeleton.Model.Friend;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;

public class FriendInfo {
    private int accountType;
    private boolean isMember;
    private boolean isVerified;
    private String name;
    private String originalName;
    private String profilePictureUrl;
    private String profilePictureUrlMedium;
    private String profilePictureUrlHigh;
    private long updateAt;

    public FriendInfo(String name, String profilePictureUrl) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    public FriendInfo(int accountType, boolean isMember, boolean isVerified, String name, String originalName,
                      String profilePictureUrl, String profilePictureUrlMedium, String profilePictureUrlHigh, long updateAt) {
        this.accountType = accountType;
        this.isMember = isMember;
        this.isVerified = isVerified;
        this.name = name;
        this.originalName = originalName;
        this.profilePictureUrl = profilePictureUrl;
        this.profilePictureUrlMedium = profilePictureUrlMedium;
        this.profilePictureUrlHigh = profilePictureUrlHigh;
        this.updateAt = updateAt;
    }

    public FriendInfo(int accountType, int isMember, int verificationStatus, String name, String originalName,
                      String profilePictureUrl, String profilePictureUrlMedium, String profilePictureUrlHigh, long updateAt) {
        this(accountType, isMember == DBConstants.IPAY_MEMBER, verificationStatus == DBConstants.VERIFIED_USER,
                name, originalName, profilePictureUrl, profilePictureUrlMedium, profilePictureUrlHigh, updateAt);
    }

    public int getAccountType() {
        return accountType;
    }

    public boolean isMember() {
        return isMember;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getName() {
        return name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public long getUpdateTime() {
        return updateAt;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    // TODO remove extra checking once medium quality profile picture becomes available in live
    public String getProfilePictureUrlMedium() {
        if (profilePictureUrlMedium != null && !profilePictureUrlMedium.isEmpty())
            return profilePictureUrlMedium;
        else
            return getProfilePictureUrl();
    }

    // TODO remove extra checking once high quality profile picture becomes available in live
    public String getProfilePictureUrlHigh() {
        if (profilePictureUrlHigh != null && !profilePictureUrlHigh.isEmpty())
            return profilePictureUrlHigh;
        else
            return getProfilePictureUrlMedium();
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "accountType=" + accountType +
                ", isMember=" + isMember +
                ", isVerified=" + isVerified +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                '}';
    }
}
