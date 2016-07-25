package bd.com.ipay.ipayskeleton.Model.Friend;

import java.util.List;

public class AddFriendRequest {
    private final List<InfoAddFriend> newFriends;

    public AddFriendRequest(List<InfoAddFriend> newFriends) {
        this.newFriends = newFriends;
    }

    public List<InfoAddFriend> getNewFriends() {
        return newFriends;
    }
}
