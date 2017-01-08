package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;


import android.content.Context;
import android.content.res.Resources;


public class UserBankClass {
    private long bankAccountId;
    private long bankBranchId;
    private int accountType;
    private String accountName;
    private String accountNumber;
    private Integer accountStatus;
    private String verificationStatus;
    private String bankName;
    private String branchName;
    private String bankCode;


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

    private String getBankCode() {
        return bankCode;
    }

    public int getBankIcon(Context context) {
        Resources resources = context.getResources();
        int resourceId;
        if(bankCode!=null)
        resourceId = resources.getIdentifier("ic_bank" + getBankCode(), "drawable",
                context.getPackageName());
        else
            resourceId = resources.getIdentifier("ic_bank" + "111", "drawable",
                    context.getPackageName());
        return resourceId;
        //return resources.getDrawable(resourceId);
    }

}

