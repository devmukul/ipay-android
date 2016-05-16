package bd.com.ipay.ipayskeleton.Model.Friend;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;

public class FriendInfo {
    private int accountType;
    private boolean isFriend;
    private boolean isVerified;
    private String name;
    private String profilePictureUrl;

    public FriendInfo() {

    }

    public FriendInfo(int accountType, boolean isFriend, boolean isVerified, String name, String profilePictureUrl) {
        this.accountType = accountType;
        this.isFriend = isFriend;
        this.isVerified = isVerified;
        this.name = name;
        this.profilePictureUrl = profilePictureUrl;
    }

    public FriendInfo(int accountType, boolean isFriend, int verificationStatus, String name, String profilePictureUrl) {
        this(accountType, isFriend, verificationStatus == DBConstants.VERIFIED_USER, name, profilePictureUrl);
    }

    public int getAccountType() {
        return accountType;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getName() {
        return name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
