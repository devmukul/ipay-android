package bd.com.ipay.ipayskeleton.Model.Friend;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FriendInfo {
    private int accountType;
    private boolean isMember;
    private boolean isVerified;
    private String name;
    private String originalName;
    private String profilePictureUrl;
    private long updateAt;

    public FriendInfo(String name, String profilePictureUrl) {
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    private FriendInfo(int accountType, boolean isMember, boolean isVerified, String name, String originalName, long updateAt, String profilePictureUrl) {
        this.accountType = accountType;
        this.isMember = isMember;
        this.isVerified = isVerified;
        this.name = name;
        this.originalName = originalName;
        this.profilePictureUrl = profilePictureUrl;
        this.updateAt = updateAt;
    }

    public FriendInfo(int accountType, int isMember, int verificationStatus, String name, String originalName, long updateAt, String profilePictureUrl) {
        this(accountType, isMember == DBConstants.IPAY_MEMBER, verificationStatus == DBConstants.VERIFIED_USER, name, originalName, updateAt, profilePictureUrl);
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
        // If the profile picture is taken from the server, then append the base url with it
        if (profilePictureUrl != null && profilePictureUrl.startsWith("/"))
            return Constants.BASE_URL_FTP_SERVER + profilePictureUrl;
        else
            return profilePictureUrl;
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
