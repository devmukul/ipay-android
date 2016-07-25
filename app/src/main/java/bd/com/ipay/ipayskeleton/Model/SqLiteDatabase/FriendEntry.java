package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

class FriendEntry {

    private final String mobileNumber;
    private final String name;
    private final int accountType;
    private final String profilePicture;
    private final int isVerified;

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
