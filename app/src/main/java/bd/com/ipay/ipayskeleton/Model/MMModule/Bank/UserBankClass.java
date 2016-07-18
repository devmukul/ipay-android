package bd.com.ipay.ipayskeleton.Model.MMModule.Bank;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;


public class UserBankClass {
    public long bankAccountId;
    public long bankBranchId;
    public int accountType;
    public String accountName;
    public String accountNumber;
    public Integer accountStatus;
    public String verificationStatus;
    public String bankName;
    public String branchName;
    public String bankCode;


    public String getBankName() {
        return bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public long getBankAccountId() {
        return bankAccountId;
    }

    public long getBankBranchId() {
        return bankBranchId;
    }

    public int getAccountType() {
        return accountType;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public String getBankCode() {
        return bankCode;
    }

    public int getBankIcon(Context context) {
        Resources resources = context.getResources();
        final int resourceId = resources.getIdentifier("bank" + getBankCode(), "drawable",
                context.getPackageName());
        return resourceId;
        //return resources.getDrawable(resourceId);
    }
}
