package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;


import android.content.Context;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;


public class UserBankClass implements Parcelable {
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

    public UserBankClass() {

    }

    protected UserBankClass(Parcel in) {
        bankAccountId = in.readLong();
        bankBranchId = in.readLong();
        accountType = in.readInt();
        accountName = in.readString();
        accountNumber = in.readString();
        verificationStatus = in.readString();
        bankName = in.readString();
        branchName = in.readString();
        bankCode = in.readString();
    }

    public static final Creator<UserBankClass> CREATOR = new Creator<UserBankClass>() {
        @Override
        public UserBankClass createFromParcel(Parcel in) {
            return new UserBankClass(in);
        }

        @Override
        public UserBankClass[] newArray(int size) {
            return new UserBankClass[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(bankAccountId);
        dest.writeLong(bankBranchId);
        dest.writeInt(accountType);
        dest.writeString(accountName);
        dest.writeString(accountNumber);
        dest.writeString(verificationStatus);
        dest.writeString(bankName);
        dest.writeString(branchName);
        dest.writeString(bankCode);
    }

    public int getBankIcon(Context context) {
        Resources resources = context.getResources();
        int resourceId;
        if (bankCode != null)
            resourceId = resources.getIdentifier("ic_bank" + getBankCode(), "drawable",
                    context.getPackageName());
        else
            resourceId = resources.getIdentifier("ic_bank" + "111", "drawable",
                    context.getPackageName());
        return resourceId;
        //return resources.getDrawable(resourceId);
    }
}

