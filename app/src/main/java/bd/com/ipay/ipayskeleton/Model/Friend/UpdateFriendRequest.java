package bd.com.ipay.ipayskeleton.Model.Friend;

import java.util.List;

public class UpdateFriendRequest {
    private final List<InfoUpdateFriend> updateFriends;

    public UpdateFriendRequest(List<InfoUpdateFriend> updateFriends) {
        this.updateFriends = updateFriends;
    }

    public List<InfoUpdateFriend> getUpdateFriends() {
        return updateFriends;
    }
}
