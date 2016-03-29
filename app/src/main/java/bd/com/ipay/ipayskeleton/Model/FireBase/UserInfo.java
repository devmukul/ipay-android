package bd.com.ipay.ipayskeleton.Model.FireBase;

public class UserInfo {
    public String name;
    public boolean isFriend;
    public int accountType;
    public String profilePictureUrl;
    public boolean isVerified;

    public UserInfo() {
    }

    public String getName() {
        return name;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
