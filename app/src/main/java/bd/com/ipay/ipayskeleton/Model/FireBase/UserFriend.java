package bd.com.ipay.ipayskeleton.Model.FireBase;

public class UserFriend {

    private String mobileNumber;
    private String name;
    private boolean friend;

    public UserFriend() {
    }

    public UserFriend(String mobileNumber, String name) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.friend = false;
    }

    public UserFriend(String mobileNumber, String name, boolean isFriend) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.friend = isFriend;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsFriend(boolean isFriend) {
        this.friend = isFriend;
    }
}
