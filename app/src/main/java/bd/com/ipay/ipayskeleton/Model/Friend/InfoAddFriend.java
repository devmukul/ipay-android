package bd.com.ipay.ipayskeleton.Model.Friend;

public class InfoAddFriend {
    private String mobileNumber;
    private String contactName;
    private String relationship;

    public InfoAddFriend() {

    }

    public InfoAddFriend(String mobileNumber, String contactName) {
        this.mobileNumber = mobileNumber;
        this.contactName = contactName;
    }

    public InfoAddFriend(String mobileNumber, String contactName, String relationship) {
        this.mobileNumber = mobileNumber;
        this.contactName = contactName;
        this.relationship = relationship;
    }

    public String getFriendsNumber() {
        return mobileNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public String getRelationship() {
        return relationship;
    }
}
