package bd.com.ipay.ipayskeleton.Model.Friend;

public class AddFriendInfo {
    private String friendsNumber;
    private String friendsName;

    public AddFriendInfo() {

    }

    public AddFriendInfo(String friendsNumber, String friendsName) {
        this.friendsNumber = friendsNumber;
        this.friendsName = friendsName;
    }

    public String getFriendsNumber() {
        return friendsNumber;
    }

    public String getFriendsName() {
        return friendsName;
    }
}
