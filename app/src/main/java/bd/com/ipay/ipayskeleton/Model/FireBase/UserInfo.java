package bd.com.ipay.ipayskeleton.Model.FireBase;

public class UserInfo {
    public String name;
    public boolean isFriend;
    public int accountType;
    public String profilePictureUrl;

    public UserInfo() {
    }

    public String getName() {
        return name;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
