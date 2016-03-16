package bd.com.ipay.ipayskeleton.Model.FireBase;

public class FriendNodeToUpload {

    public UserInfoToUpload info;
    public String phoneNumber;

    public FriendNodeToUpload(UserInfoToUpload info, String phoneNumber) {
        this.info = info;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserInfoToUpload getInfo() {
        return info;
    }
}
