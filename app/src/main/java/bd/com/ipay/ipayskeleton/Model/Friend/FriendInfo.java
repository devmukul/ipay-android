package bd.com.ipay.ipayskeleton.Model.Friend;

public class FriendInfo {
    private int accountType;
    private boolean isFriend;
    private boolean isVerified;
    private String name;
    private String profilePictureUrl;

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
