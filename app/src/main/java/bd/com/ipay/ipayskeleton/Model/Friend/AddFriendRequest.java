package bd.com.ipay.ipayskeleton.Model.Friend;

import java.util.List;

public class AddFriendRequest {
    private List<AddFriendInfo> newFriends;

    public AddFriendRequest(List<AddFriendInfo> newFriends) {
        this.newFriends = newFriends;
    }

    public List<AddFriendInfo> getNewFriends() {
        return newFriends;
    }
}
