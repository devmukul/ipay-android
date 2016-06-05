package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

public class FriendEntry {

    private String mobileNumber;
    private String name;
    private int accountType;
    private String profilePicture;
    private int isVerified;

    public FriendEntry(String mobileNumber, String name, int accountType, String profilePicture, int isVerified) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.accountType = accountType;
        this.profilePicture = profilePicture;
        this.isVerified = isVerified;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public int getIsVerified() {
        return isVerified;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
