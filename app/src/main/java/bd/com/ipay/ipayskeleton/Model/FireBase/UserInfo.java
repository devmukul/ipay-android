package bd.com.ipay.ipayskeleton.Model.FireBase;

public class UserInfo {
    public String name;
    public boolean isFriend;

    public UserInfo(String name, boolean isFriend) {
        this.name = name;
        this.isFriend = isFriend;
    }

    public String getName() {
        return name;
    }

    public boolean isFriend() {
        return isFriend;
    }
}
