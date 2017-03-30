package bd.com.ipay.ipayskeleton.Model.Friend;

import java.util.List;

public class DeleteFriendRequest {
    private final List<InfoDeleteFriend> contacts;

    public DeleteFriendRequest(List<InfoDeleteFriend> newFriends) {
        this.contacts = newFriends;
    }

    public List<InfoDeleteFriend> getNewFriends() {
        return contacts;
    }
}
