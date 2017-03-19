package bd.com.ipay.ipayskeleton.Model.Friend;

import java.util.List;

public class GetContactsResponse {
    private int totalCount;
    private List<FriendInfo> contactList;

    public int getTotalCount() {
        return totalCount;
    }

    public List<FriendInfo> getContactList() {
        return contactList;
    }

    @Override
    public String toString() {
        return "FriendInfo{" +
                "totalCount='" + totalCount + '\'' +
                ", contactList=" + contactList +
                '}';
    }
}
