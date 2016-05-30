package bd.com.ipay.ipayskeleton.Model.Friend;

public class InfoAddFriend {
    private String friendsNumber;
    private String friendsName;

    public InfoAddFriend() {

    }

    public InfoAddFriend(String friendsNumber, String friendsName) {
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
