package bd.com.ipay.ipayskeleton.Model.Friend;

public class UpdateFriendRequest {
    private String friendsNumber;
    private String friendsName;

    public UpdateFriendRequest(String friendsNumber, String friendsName) {
        this.friendsNumber = friendsNumber;
        this.friendsName = friendsName;
    }
}
