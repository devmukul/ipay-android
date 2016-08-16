package bd.com.ipay.ipayskeleton.Model.Friend;

public class InfoAddFriend {
    private String friendsNumber;
    private String friendsName;
    private String relationship;

    public InfoAddFriend() {

    }

    public InfoAddFriend(String friendsNumber, String friendsName) {
        this.friendsNumber = friendsNumber;
        this.friendsName = friendsName;
    }

    public InfoAddFriend(String friendsNumber, String friendsName, String relationship) {
        this.friendsNumber = friendsNumber;
        this.friendsName = friendsName;
        this.relationship = relationship;
    }

    public String getFriendsNumber() {
        return friendsNumber;
    }

    public String getFriendsName() {
        return friendsName;
    }

    public String getRelationship() {
        return relationship;
    }
}
