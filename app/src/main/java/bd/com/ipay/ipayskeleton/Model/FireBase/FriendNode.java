package bd.com.ipay.ipayskeleton.Model.FireBase;

public class FriendNode {
    public String phoneNumber;
    public UserInfo info;

    public FriendNode(String phoneNumber, UserInfo info) {
        this.phoneNumber = phoneNumber;
        this.info = info;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserInfo getInfo() {
        return info;
    }
}
