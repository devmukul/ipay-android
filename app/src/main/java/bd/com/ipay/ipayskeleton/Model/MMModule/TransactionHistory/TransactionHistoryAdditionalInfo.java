package bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory;

import android.content.Context;
import android.content.res.Resources;

public class TransactionHistoryAdditionalInfo {
    private String userName;
    private String userMobileNumber;
    private String userProfilePic;
    private String bankAccountNumber;
    private String bankAccountName;
    private String bankName;
    private String branchName;
    //private String bankCode;

    public TransactionHistoryAdditionalInfo() {

    }

    public TransactionHistoryAdditionalInfo(String userName, String userMobileNumber, String userProfilePic,
                String bankAccountNumber, String bankAccountName, String bankName, String branchName) {
        this.userName = userName;
        this.userMobileNumber = userMobileNumber;
        this.userProfilePic = userProfilePic;
        this.bankAccountNumber = bankAccountNumber;
        this.bankAccountName = bankAccountName;
        this.bankName = bankName;
        this.branchName = branchName;
        //this.bankCode = bankCode;
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

    public String getBankName() {
        return bankName;
    }

    public String getBranchName() {
        return branchName;
    }

  /*  public String getBankCode() {
        return bankCode;
    }

    public int getBankIcon(Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("bank" + getBankCode(), "drawable",
                context.getPackageName());
        return resourceId;
        //return resources.getDrawable(resourceId);
    }*/
}
