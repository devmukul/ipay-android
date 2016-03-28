package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

public class SubscriberEntry {

    private String mobileNumber;
    private String name;
    private int accountType;
    private String profilePicture;

    public SubscriberEntry(String mobileNumber, String name, int accountType, String profilePicture) {
        this.mobileNumber = mobileNumber;
        this.name = name;
        this.accountType = accountType;
        this.profilePicture = profilePicture;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getProfilePicture() {
        return profilePicture;
    }
}
