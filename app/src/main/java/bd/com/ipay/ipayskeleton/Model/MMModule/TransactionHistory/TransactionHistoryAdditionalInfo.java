package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

public class TransactionHistoryAdditionalInfo {
    private String userName;
    private String userMobileNumber;
    private String userProfilePic;
    private String bankAccountNumber;
    private String bankAccountName;

    public TransactionHistoryAdditionalInfo() {

    }

    public TransactionHistoryAdditionalInfo(String userName, String userMobileNumber, String userProfilePic, String bankAccountNumber, String bankAccountName) {
        this.userName = userName;
        this.userMobileNumber = userMobileNumber;
        this.userProfilePic = userProfilePic;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountName = bankAccountName;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserMobileNumber() {
        return userMobileNumber;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }
}
