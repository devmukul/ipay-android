package bd.com.ipay.ipayskeleton.Model.Friend;

import java.util.List;

public class AddFriendRequest {
    private final List<InfoAddFriend> contacts;

    public AddFriendRequest(List<InfoAddFriend> newFriends) {
        this.contacts = newFriends;
    }

    public List<InfoAddFriend> getNewFriends() {
        return contacts;
    }
}
